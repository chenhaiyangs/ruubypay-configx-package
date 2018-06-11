IndexPage = {
    init: function () {
        IndexPage.bindingEvents();
    },
    findSelectedGroup: function () {
        return $("#groupList").find("li[data-group].active:first");
    },
    bindingEvents: function () {
        /**
         * 点击组，获取组数据
         */
        $("#groupList").on("click", "li[data-group]", function(e) {
            e.preventDefault();
            var groupLink = $(this);

            var activeGroup = IndexPage.findSelectedGroup();
            activeGroup.removeClass("active");
            groupLink.addClass("active");
            activeGroup.find("a:first").hide();
            groupLink.find("a:first").show();

            var version = $("#versionDD").text().trim();
            var group = groupLink.attr("data-group");
            $.ajax({
                url: "/group/" + version + "/" + group,
                method: "get",
                success: function (data) {
                    // hack for session timeout
                    if($("#configToolkitAdmin", data).length > 0) {
                        location.href = "/login";
                    } else {
                        var dataD = $("#dataD");
                        dataD.html(data);
                        dataD.find("[name=key]:first").focus();

                        var exportGroupBt = $("#exportGroupBt");
                        exportGroupBt.attr("href", "/export/" + version + "/" + group);
                        exportGroupBt.removeClass("disabled");
                    }
                }
            });
        });

        /**
         * 移除分组
         */
        $("#groupList").on("click", "a[group]", function (e) {
            e.stopPropagation();
            e.preventDefault();
            var groupA = $(this);
            var version = groupA.attr("version");
            var group = groupA.attr("group");

            if(window.confirm("确认删除组[ " + group + "]的所有配置?")) {
                $.ajax({
                    url: "/group/" + version + "/" + group,
                    method: "delete",
                    success: function (data) {
                        if (data.suc) {
                            location.reload();
                        }else {
                            alert(data.message);
                        }
                    }
                });
            }

        });

        /**
         * 新建版本弹出层
         */
        $("#newModal").on("shown.bs.modal", function (e) {
            $(this).find("input:first").focus();
        });

        /**
         * 更新属性弹出层
         */
        $("#updatePropModal").on("shown.bs.modal", function (e) {
            $("#updateValue").focus();
        });

        /**
         * 创建或者克隆版本
         */
        $("#newVersionButton").bind("click", function (e) {
            e.preventDefault();
            var newVersionForm = $(this).parents("form:first");
            var version = newVersionForm.find("input[name=version]:first").val();
            var fromVersion = newVersionForm.find("select[name=fromVersion]:first").val();
            $.ajax({
                url: "/version/" + version,
                method: "post",
                data: {
                    "fromVersion": fromVersion
                },
                success: function (data) {
                    if(data.suc) {
                        location.href = data.body;
                    }else {
                        alert(data.message);
                    }
                }
            });
        });

        /**
         * 新建属性
         */
        $("#dataD").on("click", "button[name=newProp]", function (e) {
            e.preventDefault();
            var inputGroup = $(this).parents(".input-group:first");
            var version = inputGroup.find("[name=version]:first").val();
            var group = inputGroup.find("[name=group]:first").val();
            var key = inputGroup.find("[name=key]:first").val();
            var value = inputGroup.find("[name=value]:first").val();
            var comment = inputGroup.find("[name=comment]:first").val();
            
            $.ajax({
                url: "/create",
                method: "post",
                data: {
                    "version": version,
                    "group": group,
                    "key": key,
                    "value": value,
                    "comment": comment
                },
                success: function (data) {
                    if(data.suc) {
                        IndexPage.findSelectedGroup().click();
                    } else {
                        alert(data.message);
                    }
                }
            });
        });

        /**
         * 移除属性
         */
        $("#dataD").on("click", "a[delprop]", function (e) {
            e.preventDefault();
            var delpropA = $(this);
            var version = delpropA.attr("version");
            var group = delpropA.attr("group");
            var key = delpropA.attr("delprop");

            if(window.confirm("确认删除key[ " + key + "]的配置 ?")) {
                $.ajax({
                    url: "/delete/" + version + "/" + group + "/" + key,
                    method: "delete",
                    success: function (data) {
                        if(data.suc) {
                            IndexPage.findSelectedGroup().click();
                        }else {
                            alert(data.message);
                        }
                    }
                });
            }
        });

        /**
         * 打开编辑属性的弹窗
         */
        $("#dataD").on("click", "a[updateprop]", function (e) {
            e.preventDefault();
            var updatepropA = $(this);
            var theRow = updatepropA.parents("tr:first");

            $("#updateVersion").val(updatepropA.attr("version"));
            $("#updateGroup").val(updatepropA.attr("group"));
            $("#updateKey").val(updatepropA.attr("updateprop"));
            $("#updateValue").val(theRow.find("td[name=value]:first").text());
            $("#updateComment").val(theRow.find("td[name=comment]:first").text());
            $("#updatePropModal").modal("show");
            $("#updateValue").focus();
        });

        /**
         * 更新属性
         * @type {*|jQuery|HTMLElement}
         */
        var updatePropModal = $("#updatePropModal");
        updatePropModal.on("click", "button[name=updateButton]", function (e) {
            e.preventDefault();
            var version = $("#updateVersion").val();
            var group = $("#updateGroup").val();
            var key = $("#updateKey").val();
            var value = $("#updateValue").val();
            var comment = $("#updateComment").val();

            $.ajax({
                url: "/update",
                method: "post",
                data: {
                    "version": version,
                    "group": group,
                    "key": key,
                    "value": value,
                    "comment": comment
                },
                success: function (data) {
                    if(data.suc){
                        $("#updatePropModal").modal("hide");
                        IndexPage.findSelectedGroup().click();
                    }else {
                        alert(data.message);
                    }
                }
            });
        });

        /**
         * 展示选中的配置文件
         * @type {*|jQuery|HTMLElement}
         */
        var importFile = $("#importFile");
        importFile.on("change", function () {
            var filePath = importFile.val();
            var fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            importFile.next().html(fileName);
        });
    }
};
IndexPage.init();