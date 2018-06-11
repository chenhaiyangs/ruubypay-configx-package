[#ftl]
[#import 'common.ftl' as c]
<html lang="en">
<head>
    [@c.head/]

    <link href="/css/index.css" rel="stylesheet">
    <script src="/js/index.js" type="text/javascript" defer></script>
    <title>配置管理面板</title>
</head>

<body>
    <nav class="navbar navbar-dark bg-dark navbar-expand-lg justify-content-between fixed-top">
        <span class="navbar-brand">${root}</span>

        <ul class="navbar-nav" style="min-width: 4.5em;">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="versionDD" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    ${theVersion!"Version"}
                </a>
                <div class="dropdown-menu" aria-labelledby="versionDD">
                [#if versions??]
                    [#list versions as version]
                        <a class="dropdown-item" href="/version/${version}">${version}</a>
                    [/#list]
                [/#if]
                </div>
            </li>
        </ul>

        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <button class="btn btn-sm btn-outline-secondary mr-auto mybtn" type="button" data-toggle="modal" data-target="#newModal">新建版本</button>
            <button class="btn btn-sm btn-outline-secondary mybtn" [#if theVersion??][#else]disabled[/#if] type="button" data-toggle="modal" data-target="#importModal">导入</button>
            <button class="btn btn-sm btn-outline-secondary mybtn" [#if theVersion??][#else]disabled[/#if] type="button" data-toggle="modal" data-target="#exportModal">导出</button>
            <a href="/logout"><img class="ml-4" style="margin-top: 0.4em;" src="/image/account-logout.svg"></a>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <!-- 右侧分组列表 -->
            <div class="col-3" style="font-size: smaller;">
                <div class="groups">
                    <ul class="list-group" id="groupList">
                        [#if groups??]
                            [#list groups as group]
                                <li data-group="${group}" class="list-group-item d-flex justify-content-between align-items-center">
                                    ${group}
                                    <a href="#" version="${theVersion}" group="${group}" style="display: none"><img src="/image/trash.png"></a>
                                </li>
                            [/#list]
                        [/#if]
                    </ul>
                </div>
                <form action="/group/${theVersion!""}" method="post" style="margin-top: 20px">
                    <div class="input-group mt-2" style="margin-top: 1em;">
                        <input required spellcheck="false" name="newGroup" class="form-control" style="font-size: small;" placeholder="请输入组名" aria-label="group name" aria-describedby="basic-addon2">
                        <div class="input-group-append">
                            <button class="btn btn-outline-secondary" style="font-size: small;" [#if theVersion??][#else]disabled[/#if] >新增</button>
                        </div>
                    </div>
                </form>
            </div>
            <!-- 配置集列表 -->
            <div class="col-9" style="font-size: small;" id="dataD">
                <div class="text-center mt-5" style="opacity: 0.3;"></div>
            </div>
        </div>
    </div>

    <!-- 新建版本弹出层 -->
    <div class="modal fade" id="newModal" tabindex="-1" role="dialog" aria-labelledby="newModalLabel">
        <div class="modal-dialog" role="document" style="width: 29em;">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">新建版本</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span >&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form method="post" action="/version">
                        <div class="form-row align-items-center">
                            <div class="col-auto">
                                <label class="sr-only" for="versionInput">请输入版本</label>
                                <input required name="version" class="form-control" style="width:8em;" id="versionInput" placeholder="请输入版本">
                            </div>
                            <div class="col-auto">
                                <label class="sr-only" for="inlineFormInputGroup">Clone</label>
                                <div class="input-group">
                                    <div class="input-group-prepend">
                                        <div class="input-group-text">克隆自</div>
                                    </div>
                                    <select class="form-control custom-select mr-sm-2" name="fromVersion" id="inlineFormInputGroup">
                                        <option value="" selected>请选择版本</option>
                                        [#if versions??]
                                            [#list versions as version]
                                                <option value="${version}">${version}</option>
                                            [/#list]
                                        [/#if]
                                    </select>
                                </div>
                            </div>
                            <div class="col-auto">
                                <button id="newVersionButton" class="btn btn-outline-dark">提交</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- 更新配置 -->
    <div class="modal fade" id="updatePropModal" tabindex="-1" role="dialog" aria-labelledby="updatePropLabel" data-backdrop="false" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">更新配置信息</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span>&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-group row">
                        <label for="updateKey" class="col-sm-2 col-form-label">Key</label>
                        <div class="col-sm-10">
                            <input type="text" readonly class="form-control-plaintext" id="updateKey">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="updateValue" class="col-sm-2 col-form-label">Value</label>
                        <div class="col-sm-10">
                            <input type="text" required class="form-control" id="updateValue">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="updateComment" class="col-sm-2 col-form-label">注释</label>
                        <div class="col-sm-10">
                            <input type="text" required class="form-control" id="updateComment">
                        </div>
                    </div>
                    <input type="hidden" name="updateVersion" id="updateVersion">
                    <input type="hidden" name="updateGroup" id="updateGroup">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-dark" name="updateButton">保存</button>
                    <button type="button" class="btn btn-outline-dark" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 导出配置 -->
    <div class="modal fade" id="exportModal" tabindex="-1" role="dialog" aria-labelledby="exportLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">导出配置文件</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span>&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <a href="/export/${theVersion!""}" class="btn btn-outline-dark btn-lg btn-block" id="exportVersionBt">导出当前版本(.zip)</a>
                    <a href="#" class="btn btn-outline-dark btn-lg btn-block disabled" id="exportGroupBt">导出当前选中组(.properties)</a>
                </div>
            </div>
        </div>
    </div>

    <!-- 导入配置文件 -->
    <div class="modal fade" id="importModal" tabindex="-1" role="dialog" aria-labelledby="importLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">请上传您的配置文件(.zip或者.properties)</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form action="/import/${theVersion!""}" method="post" enctype="multipart/form-data">
                        <div class="input-group mb-3">
                            <div class="custom-file">
                                <input type="file" name="file" class="custom-file-input" id="importFile">
                                <label class="custom-file-label" for="importFile">选择文件</label>
                            </div>
                            <div class="input-group-append">
                                <button class="btn btn-outline-secondary">导入配置</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>