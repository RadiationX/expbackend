<?php

class foo_mysqli extends mysqli {
    public function __construct($host, $user, $pass, $db, $port) {
        parent::init();

        if (!parent::options(MYSQLI_INIT_COMMAND, 'SET AUTOCOMMIT = 0')) {
            die('Установка MYSQLI_INIT_COMMAND завершилась провалом');
        }

        if (!parent::options(MYSQLI_OPT_CONNECT_TIMEOUT, 5)) {
            die('Установка MYSQLI_OPT_CONNECT_TIMEOUT завершилась провалом');
        }

        if (!parent::real_connect($host, $user, $pass, $db, $port)) {
            die('Ошибка подключения (' . mysqli_connect_errno() . ') '
                    . mysqli_connect_error());
        }
    }
}

$db = new foo_mysqli('mariadb', 'mysqlusr', 'mysqlpwd', 'mysqldb', '3306');
$listdbtables = array_column($db->fetch_all($link->query('SHOW TABLES')),0);
echo $listdbtables;

echo 'Выполнено... ' . $db->host_info . "\n";

$db->close();
?>