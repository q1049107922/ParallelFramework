package com.parallel.framework;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


/**
 * Created by b_lin
 * https://docs.microsoft.com/en-us/archive/msdn-magazine/2009/april/parallelizing-operations-with-dependencies
 */
public abstract class Processer<Req,Resp> extends Level implements Cloneable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 预期耗时 （单位毫秒）
     */
    public int expectCostTime = 5;

    protected static ApplicationContext springContext = ContextHolder.getApplicationContext();

    private DealRequestContext<Req, Resp> requestContext;

    protected List<Processer> dependProcessers;

    protected int retryTimes = 2;

    protected int timeOut = 5000;

    private ProcessContext processContext;

    private String requestStr;
    private String responseStr;

    private boolean sameProc=false;

    /**
     * 处理器统一的 log 信息
     */
    private FullLogEntity logEntity = new FullLogEntity();

    private Result<DealRequestContext<Req, Resp>> finalResult;

    private Req request;

    private Resp response;
    long startTime;
    public Processer(){
        //dependProcessers = getDependProcessers();
    }


    public DealRequestContext<Req, Resp> getRequestContext() {
        return requestContext;
    }

    public ProcessContext getProcessContext(){
        return processContext;
    }

    private int retryTimes() {
        return retryTimes;
    }

    /**
     * 可以使用非注解注入,支持动态增加注入
     * @return
     */
    protected List<Processer> getDependProcessers(){
        return new ArrayList<>();
    }

    protected void addEvent(String event) {
        logEntity.addEvent(event);
    }

    /**
     * 降级 mock 一个Response
     * @return
     */
    protected Resp degradeResponse() {
        Resp newUsr = null;
        try {
            // 通过反射获取model的真实类型
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<Resp> clazz = (Class<Resp>) pt.getActualTypeArguments()[1];
            // 通过反射创建model的实例
            newUsr = clazz.newInstance();
        } catch (Exception e) {
            logger.error("degradeResponse()", e);
        }
        return newUsr;
        //return null;
    }

    /**
     * 获取依赖 (with 注入)
     * @return
     */
    protected List<Processer> getDepends() {
        if (dependProcessers == null) {
            dependProcessers = getDependProcessers();
            if (dependProcessers == null) {
                dependProcessers = new ArrayList<>();
            }
            // 根据 @Dependency 注入依赖关系
            try {
                //Field[] fields = this.getClass().getDeclaredFields();
                //具有继承关系的依赖
                List<Field> fields = new ArrayList<>();
                Class tempClass = this.getClass();
                while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
                    fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
                    tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
                }
                for (Field field : fields) {
                    field.setAccessible(true); // 私有属性必须设置访问权限
                    Dependency annotation = field.getAnnotation(Dependency.class);
                    if (annotation != null) {
                        Processer proc = (Processer) field.get(this);
                        if (proc == null) {
                            proc = createProcesser(field.getType());
                            /*try {
                                //从spring中获取bean
                                if (springContext != null) {
                                    Object obj = springContext.getBean(field.getType());
                                    if (obj != null) {
                                        proc = (Processer) obj;
                                        field.set(this, proc);
                                    }
                                }
                            } catch (BeansException e) {
                            }
                            //spring中拿不到就实例化
                            if (proc == null) {
                                proc = (Processer) field.getType().newInstance();
                                field.set(this, proc);
                            }*/
                            field.set(this, proc);
                        }
                        if (!dependProcessers.contains(proc)) {
                            dependProcessers.add(proc);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("getDepends()", e);
            }
        }
        return dependProcessers;
    }

    protected Processer createProcesser(Class clazz) throws Exception {
        Processer proc = null;
        try {
            //从spring中获取bean
            if (springContext != null) {
                proc = (Processer) springContext.getBean(clazz);
                return proc;
            }
        } catch (BeansException e) {
        }
        //spring中拿不到就实例化
        proc = (Processer) clazz.newInstance();
        return proc;
    }


    /**
     * 业务全部执行结束后会调用该方法
     */
    public void dispose() {

    }

    protected abstract Req getRequest(ProcessContext context);

    /**
     * 执行（异常可能会重试执行）
     * @param request
     * @return 最外层的Result一般返回成功；除非某个处理器异常需要中断整个请求时，才会返回失败
     * @throws Exception
     */
    protected abstract Result<DealRequestContext<Req, Resp>> executeContext(Req request) throws Exception;

    protected void before() {
    }

    protected void after() {
    }

    public Req getRequest() {
        return requestContext.getRequest();
    }

    /**
     * 执行
     * @param context
     * @return
     */
    public Result execute(ProcessContext context) {
        processContext = context;
        try {
            before();
            request = getRequest(context);
            finalResult = executeInCache(context);
            if (finalResult == null) {
                // log
                logEntity.setStart(new Date());
                logEntity.setType(getRequestTypeForLog());
                logEntity.setRequest(request);
                logEntity.setProcesser(this.getClass().getSimpleName());
                finalResult = executeContext(request);
            } else {
                sameProc = true;
            }
            if (finalResult.getCode().equals(ResultCodeEnum.success)) {
                requestContext = finalResult.getObj();
                context.getProcessers().add(this);
            }
            return finalResult;
        } catch (Exception e) {
            logger.error(this.getClass().getName(),e);
            finalResult = new Result<>(ResultCodeEnum.exception);
        } finally {
            after();
            exec();
        }
        return finalResult;
    }

    /**
     * 在已执行的处理器中获取结果
     * @param context
     * @return
     * @throws Exception
     */
    private Result<DealRequestContext<Req, Resp>> executeInCache(ProcessContext context) throws Exception {
        Result<DealRequestContext<Req, Resp>> result = null;

        requestStr = getRequestStr();

        // in cache
        for (Processer processer : context.getProcessers().stream().filter(m -> m.hasExec()).collect(Collectors.toList())) {
            if (processer.getClass().equals(this.getClass())) {
                if (requestStr.equals(processer.requestStr)) {
                    logEntity.addEvent("Get Result in Cache ");
                    logEntity.setType(getRequestTypeForLog());
                    logEntity.setProcesser(this.getClass().getSimpleName());
                    return processer.getFinalResult();
                }
            }
        }
        return null;
    }

    private String getRequestTypeForLog() {
        if (request != null) {
            return request.getClass().getSimpleName();
        } else if (response != null) {
            return response.getClass().getSimpleName();
        } else {
            return this.getClass().getSimpleName();
        }
    }

    public String getRequestStr() {
        if (requestStr == null) {
            requestStr = JsonUtil.beanToJson(request);
        }
        return requestStr;
    }

    public String getResponseStr(){
        if(responseStr==null){
            responseStr= JsonUtil.beanToJson(response);
        }
        return requestStr;
    }


    /**
     * possible null!!!!
     * @return possible null!!!
     */
    public Resp getResponse() {
        if (response != null) {
            return response;
        }
        try {
            if (getFinalResult() != null && getFinalResult().getObj() != null) {
                response = getFinalResult().getObj().getResponse(timeOut, TimeUnit.MILLISECONDS);
            }
            logEntity.setRequest(request);
            logEntity.setEnd(new Date());
            logEntity.setResponse(response);
            logEntity.setProcesser(this.getClass().getSimpleName());
            return response;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // 异常重试
            logger.error("", e);
            retryTimes = retryTimes - 1;
            if (retryTimes >= 0) {
                logEntity.addEvent(" start retry times: " + retryTimes);
                try {
                    finalResult = executeContext(getRequest());
                } catch (Exception e1) {
                    logger.error("254" + JsonUtil.beanToJson(logEntity), e1);
                }
                return getResponse();
            }
        } catch (Exception e) {
            logger.error("259" + JsonUtil.beanToJson(logEntity), e);
        } finally {
            if(!sameProc) {
                logger.info(JsonUtil.beanToJson(logEntity));
            }
        }
        return degradeResponse();
    }

    /**
     *
     * @return
     */
    public Result<DealRequestContext<Req, Resp>> getFinalResult() {
        if (finalResult == null) {
            logger.error(this.getClass() + ",finalResult is null , maybe has not executed");
        }
        return finalResult;
    }

}
