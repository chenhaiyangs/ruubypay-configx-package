[#ftl]
<style type="text/css">
    td {
        text-align: left;
        border:1px solid #ccc;
    }
    th {
        background-color: #b9bbbe;
        text-align: left;
        border:1px solid #ccc;
    }
</style>
<div class="input-group d-flex justify-content-end mb-2">
    <div class="input-group-prepend">
        <input name="key" required spellcheck="false" class="form-control" style="font-size: small;" placeholder="Key" aria-label="Key" aria-describedby="basic-addon2">
        <input name="value" spellcheck="false" class="form-control" style="font-size: small;" placeholder="Value" aria-label="Value" aria-describedby="basic-addon2">
        <input name="comment" spellcheck="false" class="form-control" style="font-size: small;" placeholder="请输入注释" aria-label="Comment" aria-describedby="basic-addon2">

        <input type="hidden" name="version" value="${version}">
        <input type="hidden" name="group" value="${group}">
    </div>
    <button name="newProp" class="btn btn-outline-secondary" style="font-size: small;" type="button">新增配置</button>
</div>
<div class="table-responsive datas">
    <table class="table table-sm" style="border:1px solid #ccc">
        <thead>
            <tr>
                <th width="20%" colspan="2">操作</th>
                <th width="20%">Key</th>
                <th width="30%">Value</th>
                <th width="30%">注释</th>
            </tr>
        </thead>
        <tbody>
        [#if items??]
            [#list items as item]
                <tr>
                    <td width="10%" style="text-align: center">
                        <div style="width: 4em;">
                            <a version="${version}" group="${group}" updateprop="${item.name}" href="#"><img src="/image/pencil.png"></a>
                        </div>
                    </td>
                    <td width="10%" style="text-align: center">
                        <div style="width: 4em;">
                            <a version="${version}" group="${group}" delprop="${item.name}" href="#"><img src="/image/trash.png"></a>
                        </div>
                    </td>
                    <td width="20%" name="name">${item.name}</td>
                    <td width="30%" name="value">${item.value}</td>
                    <td width="30%" name="comment">${item.comment!""}</td>
                </tr>
            [/#list]
        [/#if]
        </tbody>
    </table>
</div>