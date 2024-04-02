layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    $.post(ctx+"/user/queryAllSales",function (res) {

        for (var i = 0; i < res.length; i++) {
            console.log(res[i].uname)
            if($("input[name='man']").val() == res[i].id ){
                $("#assignMan").append("<option value=\"" + res[i].id + "\" selected='selected' >" + res[i].uname + "</option>");
            }else {
                $("#assignMan").append("<option value=\"" + res[i].id + "\">" + res[i].uname + "</option>");
            }
        }
        //重新渲染
        layui.form.render("select");
    });


    form.on("submit(addOrUpdateSaleChance)", function (data) {
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        //弹出loading
        //通过营销机会的id来判断当前需要执行添加操作还是修改操作
        //如果营销机会的id为空，则表示执行添加操作；如果id不为空，则表示执行更新操作
        //通过获取隐藏域中的id
        var url=ctx + "/sale_chance/add";
        if($("input[name='id']").val()){
            url=ctx + "/sale_chance/update";
        }

        $.post(url, data.field, function (res) {
            if (res.code == 200) {
                setTimeout(function () {
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    layer.closeAll("iframe");
                    //刷新父页面
                    parent.location.reload();
                }, 500);
            } else {
                layer.msg(
                    res.msg, {
                        icon: 5
                    }
                );
            }
        });
        return false;
    });

    // 关闭弹出层
    $("#closeBtn").click(function (){
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name);//先得到当前iframe层的索引
        parent.layer.close(index);//再执行关闭
    })
});