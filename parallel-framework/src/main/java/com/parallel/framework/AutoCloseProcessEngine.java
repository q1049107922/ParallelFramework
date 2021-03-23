package com.parallel.framework;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoCloseProcessEngine implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(AutoCloseProcessEngine.class);

    private List<Processer> forExec = new ArrayList<>();

    public AutoCloseProcessEngine(List<Processer> processerList) {
        forExec=processerList;
        //calculateProcessers(processerList);
    }

    private void calProcesser(Processer processer) {
        List<Processer> depends = processer.getDepends();
        if (!forExec.contains(processer)) {
            forExec.add(processer);
        }
        if (depends != null && !depends.isEmpty()) {
            for (Processer pro : depends) {
                calProcesser(pro);
            }
        }
    }

    /**
     * 计算优先级
     */
    private void calculateLevel() {
        for (Processer processer : forExec) {
            // 初始化级别
            processer.setLevel(100);
            List<Processer> sons = new ArrayList<>();
            if (processer.getDepends() != null) {
                sons.addAll(processer.getDepends());
            }
            while (sons != null && !sons.isEmpty()) {
                // 有依赖则级别增长
                processer.increaseLevel();
                List<Processer> grandSons = new ArrayList<>();
                for (Processer son : sons) {
                    if (son.getDepends() != null) {
                        grandSons.addAll(son.getDepends());
                    }
                }
                sons = grandSons;
            }
        }
        //Collections.sort(forExec);
        //forExec.sort(Comparator.comparing(Level::getLevel));
    }

    private void calculateProcessers(List<Processer> processerList) {
        for (int i = 0; i < processerList.size(); i++) {
            calProcesser(processerList.get(i));
        }
        calculateLevel();
    }

    /**
     * 执行所有处理器
     *
     * @param processContext
     * @return
     */
    public Result execute(ProcessContext processContext) {
        StringBuilder sb = new StringBuilder("start execute : ");
        Processer processer = getProcForExec();
        while (processer != null) {
            sb.append(String.format("{%s , Level:%s}--->", processer.getClass().getSimpleName(), processer.getLevel()));
            Result result = processer.execute(processContext);
            // terminate 终止后续请求
            if (result.getCode().equals(ResultCodeEnum.terminate)) {
                return result;
            }
            processer = getProcForExec();
        }
        sb.append(" end ");
        logger.info(sb.toString());
        return Result.success;
    }

    /**
     * 获取一个未被执行，并且最优先的处理器
     *
     * @return
     */
    private Processer getProcForExec() {
        //每次都要重新计算处理器执行关系，因为会有动态插入处理器的情况
        calculateProcessers(forExec);
        Processer proc = forExec.stream().filter(m -> !m.hasExec()).sorted().findFirst().orElse(null);
        return proc;
    }


    /**
     * important! 处理器资源的释放
     */
    protected void dispose() {
        forExec.forEach(m -> m.dispose());
    }

    @Override
    public void close() throws IOException {
        dispose();
    }
}
