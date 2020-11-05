//声明控制器
app.controller('brandController',function ($scope,$controller,brandService){
    $controller('baseController',{$scope:$scope});//继承	$controller也是angular提供的一个服务，可以实现伪继承，实际上就是与BaseController共享$scope
    $scope.findAll=function (){
        //访问后台
        brandService.findAll().success(
            function (response){
                $scope.list=response
            }
        )
    }



    //重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        //$scope.findPage( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }
//分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };
//分页
    $scope.findPage=function(page,rows){
        brandService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //新增
    $scope.save=function(){
        if($scope.entity.id!=null){//如果有ID
            brandService.update($scope.entity).success(
                function(response){
                    if(response.success){
                        //重新查询
                        $scope.reloadList();//重新加载
                    }else{
                        alert(response.message);
                    }
                }
            );
        }else{
            brandService.add($scope.entity).success(
                function(response){
                    if(response.success){
                        //重新查询
                        $scope.reloadList();//重新加载
                    }else{
                        alert(response.message);
                    }
                }
            );
        }
    }

    $scope.findOne=function (id){
        brandService.findOne(id).success(
            function (response){
                $scope.entity=response
            }
        )
    }


    //定义选中该的复选框的id的数组对象
    $scope.selectIds=[];
    //修改id数组对象
    $scope.updateSelection=function ($event,id){
        if ($event.target.checked){//如果被选中就添加到数组中
            $scope.selectIds.push(id);
        }else{
            //使用js原生语言
            //如果取消选中个结果就从selectIds中移除掉那个id；
            var idx=$scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);
        }
        // alert($scope.selectIds)
    }
    //删除方法
    $scope.dele=function (){
        brandService.dele($scope.selectIds).success(
            function (response){
                if (response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message)
                }
            }
        )
    }

    //条件拼接
    $scope.searchEntity={};
    $scope.search=function (page,rows){
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }
});