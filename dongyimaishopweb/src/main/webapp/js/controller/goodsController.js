//控制层
app.controller('goodsController', function ($scope, $controller, $location, typeTemplateService, uploadService, itemCatService, goodsService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];//获取参数值
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //像富文本编辑器中添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //图片回显
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //扩展属性列表显示
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //规格回显
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //商品规格sku回显
                //sku列表格式转换
                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec)
                }

            }
        );
    }
    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue = function (specName, optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;//将查到的规格信息赋值给items
        var object = $scope.searchObjectByKey(items, 'attributeName', specName);
        if (object == null) {
            return false;
        } else {
            if (object.attributeValue.indexOf(optionName) >= 0) {
                return true;
            } else {
                return false;
            }
        }
    }
    //保存
    $scope.save = function () {
        //提取富文本编辑器的内容
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    // $scope.entity = {};
                    //重新查询
                    // $scope.reloadList();//重新加载

                    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};
                    editor.html('');//清除富文本编辑器对象
                    location.href="goods.html";//跳转到商品列表页
                } else {
                    alert(response.message);
                }
            }
        );
    }
    //保存
    $scope.add = function () {
        //提取富文本编辑器的内容
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {

                    //$scope.entity={};
                    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};
                    editor.html('');//清除富文本编辑器对象
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }
    $scope.status = ['未审核', '已审核', '审核未通过', '已驳回']//定义商品状态
    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    /*上传图片*/
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {//如果上传成功，取出url
                $scope.image_entity.url = response.message;//设置文件地址
            } else {
                alert(response.message);
            }
        }).error(function () {
            alert("上传发生错误");
        });
    };
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
//列表中移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //商品分类实现一级下拉菜单
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    }
    //实现二级分类下拉选择框
    //读取二级分类,$watch方法用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数。
    $scope.$watch('entity.goods.category1Id', function (newValue) {
        //判断一级分类有选择具体分类值，在去获取二级分类
        //根据选择的值，查询二级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;

            }
        )
    })
//实现二级分类下拉选择框
    //读取二级分类,$watch方法用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数。
    $scope.$watch('entity.goods.category2Id', function (newValue) {
        //判断一级分类有选择具体分类值，在去获取二级分类
        //根据选择的值，查询二级分类

        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;

            }
        )
    })
    //读取模板id
    //三级分类后，读取模板id
    $scope.$watch('entity.goods.category3Id', function (newValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;//更新模板id
            }
        )
    })

    //模板id选择后，更新品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue) {
        if (newValue) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    $scope.typeTemplate = response;//获取模板类型
                    $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//品牌列表
                    //如果在新增界面没有传过来id就说明不是修改界面，就执行下面的参数
                    if ($location.search()['id'] == null) {
                        $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems)//扩展属性
                    }
                }
            )
            //模板ID选择后  更新模板对象,将模板id传给	typeTemplateService中的findSpecList方法
//查询规格列表
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList = response;
                }
            );

        }
    });
    $scope.updateSpecAttribute = function ($event, name, value) {
        //查询当前所勾选的选项是否存在于 目前的规格选项中
        //人为设定 specificationItems [] name为规格名称
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        if (object != null) {
            //判断 该规格选项是否勾选 如果勾选则 推送数组 否则 删除数组中元素
            if ($event.target.checked) {
                object.attributeValue.push(value);//如果勾选了规格，就把value值添加进attributeValue中

            } else {
                //取下勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1)//后移一位，移除选项
                //如果选项都移除了，将此条记录移除
                //如果 规格选项数组中 的 选项对象中 一个值都没有 则需要将该对象从 规格选项数组中移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1);

                }
            }
        } else {
            //如果没有勾选过该类型的规格 那么就push一个具有 人为设定格式的对象
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    }
//创建sku列表//创建矩阵对象
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];////创建sku 的模版
        //遍历用户选择规格选项列表
        //[{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","64G"]}]
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            //制作矩阵对象数组
            //{"attributeName":"网络","attributeValue":["移动3G","移动4G"]}
            // {spec:{'网络':'移动3G'},price:0,num:99999,status:'0',isDefault:'0'}，{spec:{'网络':'移动4G'},price:0,num:99999,status:'0',isDefault:'0'}
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }
//添加列值
    var addColumn = function (list, columnName, columnValues) {
        //定义新的数组 作为返回值
        var newList = [];
        //循环遍历 list
        //{spec:{},price:0,num:99999,status:'0',isDefault:'0'}
        // {spec:{'网络':'移动3G'},price:0,num:99999,status:'0',isDefault:'0'}，
        // {spec:{'网络':'移动4G'},price:0,num:99999,status:'0',isDefault:'0'}
        for (var i = 0; i < list.length; i++) {
            //{spec:{},price:0,num:99999,status:'0',isDefault:'0'}
            var oldRow = list[i];
            //{"attributeName":"网络","attributeValue":["移动3G","移动4G"]}
            //["移动3G","移动4G"]
            // {"attributeName":"机身内存","attributeValue":["16G","64G"]}]
            for (var j = 0; j < columnValues.length; j++) {
//深克隆 {spec:{},price:0,num:99999,status:'0',isDefault:'0'}
                var newRow = JSON.parse(JSON.stringify(oldRow))
                // {spec:{'网络':'移动3G'},price:0,num:99999,status:'0',isDefault:'0'}
                // {spec:{'网络':'移动3G'，'机身内存':'16G'},price:0,num:99999,status:'0',isDefault:'0'}
                newRow.spec[columnName] = columnValues[j];
                //将拼接好的临时变量存入 返回值 数组中
                // {spec:{'网络':'移动3G'},price:0,num:99999,status:'0',isDefault:'0'}，{spec:{'网络':'移动4G'},price:0,num:99999,status:'0',isDefault:'0'}
                // [{spec:{'网络':'移动3G'，'机身内存':'16G'},price:0,num:99999,status:'0',isDefault:'0'},
                // {spec:{'网络':'移动3G'，'机身内存':'64G'},price:0,num:99999,status:'0',isDefault:'0'},
                // {spec:{'网络':'移动4G'，'机身内存':'16G'},price:0,num:99999,status:'0',isDefault:'0'},
                // {spec:{'网络':'移动4G','机身内存':'64G'},price:0,num:99999,status:'0',isDefault:'0'}]
                newList.push(newRow);
            }
        }
        return newList;
    }
    $scope.itemCatList = [];//定义标题数组
//加载商品分类列表
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        )
    }
});	