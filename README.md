# ParallelFramework
A framework for parallel operations with complex dependencies 具有复杂依赖关系的并行操作框架

test里面有Demo，大致运行思路如下，将业务抽象成一个一个的处理器，尽可能的并行执行

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

##### 处理器只关注直接依赖，不关注间接依赖：
E依赖C和D，同时C依赖A和B，对于E来说E并不知道A和B的存在。如果E的执行需要直接依赖B，那么直接在E里面用@Dependency声明依赖B即可。

##### 处理器间的重复依赖：
F依赖与C，E也依赖于C，并不意味着C要执行两遍。
再获取依赖处理器结果的时候会率先去已执行的处理器集合中查找**类型一致的，并且getRequest请求参数完全一致**的时候，会直接复用已执行的结果，避免重复执行。


