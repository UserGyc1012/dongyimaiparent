app.service('loginService',function ($http) {
    //列表数据保存到表单
    this.showName=function(){
        return $http.get('../login/name.do');
    }

})