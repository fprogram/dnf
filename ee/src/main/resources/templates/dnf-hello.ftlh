<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ДНФ считатель</title>
    <!-- Compiled and minified CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">

    <!-- Compiled and minified JavaScript -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>

    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">


</head>
<body>

<nav>
    <div class="nav-wrapper blue">

        <a href="/" class="brand-logo"><i class="material-icons">business</i>Сервисы - ДНФ</a>
        <a href="#" data-target="mobile-demo" class="sidenav-trigger"><i class="material-icons">menu</i></a>
        <ul class="right hide-on-med-and-down">
            <li><a href="http://185.189.14.9/">Главная</a></li>
            <li><a href="http://185.189.14.9/vfv">Великие фразы великих</a></li>
            <li class="active"><a href="http://185.189.14.9/services.html">Сервисы</a></li>
        </ul>
    </div>
</nav>

<ul class="sidenav" id="mobile-demo">
    <li><a href="http://185.189.14.9/">Главная</a></li>
    <li><a href="http://185.189.14.9/vfv">Великие фразы великих</a></li>
    <li class="active"><a href="http://185.189.14.9/services.html">Сервисы</a></li>
</ul>

<div class="container center">

    <h2>ДНФ Считалка онлайн</h2>

    <form method="post" action="/process">
        <input class="input" type="text" name="vec" size="32" placeholder="Введите вектор значений">
        <input class="btn" type="submit" value="Решить!">
    </form>

    <br/><br/><br/>

</div>

<footer class="page-footer blue">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">Blacklosh</h5>
                <p class="grey-text text-lighten-4">Backend веб-разработка</p>
                <p class="grey-text text-lighten-4">Архитектура проектов</p>
                <p class="grey-text text-lighten-4">Менторство</p>
                <p class="grey-text text-lighten-4">Мобильная робототехника</p>
            </div>
            <div class="col l4 offset-l2 s12">
                <h5 class="white-text">Я в соц. сетях:</h5>
                <ul>
                    <li><a class="grey-text text-lighten-3" href="https://vk.com/httprequest">vk.com/httprequest</a></li>
                    <li><a class="grey-text text-lighten-3" href="https://t.me/blacklosh">@blacklosh</a></li>
                    <li><a class="grey-text text-lighten-3" href="mailto:fedor.gusev.02@inbox.ru">fedor.gusev.02@inbox.ru</a></li>
                    <li><a class="grey-text text-lighten-3" href="https://discordapp.com/users/474882950945701898/">designer#5975</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            © 2022 Blacklosh Copyright
            <a class="grey-text text-lighten-4 right" href="https://vk.com/itis_kfu">KFU, ITIS</a>
        </div>
    </div>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
<script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>

<script type="text/javascript">

    M.AutoInit();
</script>

</body>

</html>
