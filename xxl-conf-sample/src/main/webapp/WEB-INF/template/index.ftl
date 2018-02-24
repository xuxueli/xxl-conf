<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>配置中心Sample</title>
</head>
<body>

<center>
    <table border=1 bordercolor="#000000" style="border-collapse:collapse" >
        <thead>
            <tr>
                <td>方式</td>
                <td>Key</td>
                <td>Value</td>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>1、XML方式</td>
                <td>default.key01</td>
                <td>${key01}</td>
            </tr>
            <tr>
                <td>2、@XxlConf方式</td>
                <td>default.key02</td>
                <td>${key02}</td>
            </tr>
            <tr>
                <td>2、API方式</td>
                <td>default.key03</td>
                <td>${key03}</td>
            </tr>
        </tbody>
    </table>
</center>

</body>
</html>