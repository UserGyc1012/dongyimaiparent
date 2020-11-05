//定义service层
app.service('brandService',function ($http){
    //查询所有的
    this.findAll=function (){
        //访问后台
        return	$http.get('../brand/findAll.do')}
    //分页
    this.findPage=function(page,rows){
        return	$http.get('../brand/findPage.do?page='+page+'&rows='+rows)
    }
    //保存
    this.add=function (entity){
        return	$http.post('../brand/save.do',entity)}
    //保存
    this.update=function (entity){
        return	$http.post('../brand/update.do',entity)}
    //删除
    this.dele=function (ids){
        return	$http.get('../brand/dele.do?ids='+ids)}
    //数据回显
    this.findOne=function (id){
        return	$http.get('../brand/findOne.do?id='+id)}
    //搜索
    this.search = function (page,rows,searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&rows='+rows,searchEntity);
    }
//下拉列表数据
    this.selectOptionList=function(){
        return $http.get('../brand/selectOptionList.do');
    }
})