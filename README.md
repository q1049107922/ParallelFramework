# ParallelFramework
A framework for parallel operations with complex dependencies 具有复杂依赖关系的并行操作框架

test里面有Demo，大致运行思路如下，尽可能并行

      A	    B        D
       \   /        /
         C         / 
         |  \     /
         |   \   /
         F     E
          \   /
            G 
			
