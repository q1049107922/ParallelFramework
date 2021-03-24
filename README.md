# ParallelFramework
A framework for parallel operations with complex dependencies 具有复杂依赖关系的并行操作框架

### 原理说明
该系统适用于接口IO交互较多并且接口相互关系复杂的系统。test里面有Demo，大致运行思路如下，将业务接口抽象成一个一个的处理器，一共有A、B、C、D、E、F、G 7个处理器，C依赖与AB，F依赖于C，E依赖于CD，G依赖于EF，执行关系如下图，尽可能的并行执行

     A     B       D
      \   /       /
        C        / 
        | \     /
        |  \   /
        F    E
         \  /
           G 
     			
执行顺序：A、B、D最先执行，然后执行C，然后执行F、E，最后执行G。

C依赖于A和B
那么直接在E里面用@Dependency声明依赖关系即可：
    
	 @Dependency
     AaaProc aaaProc;
     @Dependency
     BbbProc bbbProc;

框架会自动去处理执行顺序问题，确保C执行之前，A和B优先被执行。

### 处理器只关注直接依赖，不关注间接依赖：
E依赖C和D，同时C依赖A和B，对于E来说E并不知道A和B的存在。如果E的执行需要直接依赖B，那么直接在E里面用@Dependency声明依赖B即可。

### 处理器间的重复依赖：
F依赖与C，E也依赖于C，并不意味着C要执行两遍。
再获取依赖处理器结果的时候会率先去已执行的处理器集合中查找**类型一致的，并且getRequest请求参数完全一致**的时候，会直接复用已执行的结果，避免重复执行。

### 处理器支持Spring注入
处理器支持Spring注入，但是处理器需要保持多例，因为处理器含有私有变量。

### maven：
      
     <dependency>
       <groupId>com.parallel.framework</groupId>
       <artifactId>parallel-framework</artifactId>
       <version>1.0.0</version>
     </dependency>
     
### 备注说明
该框架只提供了最基础的代码实现，并且已经在公司核心系统生产应用。
生产最多支持过40+个处理器的情况，qps：200+，运行1年左右，运行表现良好。
处理器的执行以及调度虽然耗费少量性能，但是简洁了复杂的调用关系，提高了代码的可读性。
里面还有很多可以优化的空间，欢迎各位coder提issues。
