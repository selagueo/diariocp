<?php
  $login = new PHP_Login_Form;
  $login->ajax = true;

  $login->from_password = $_POST['password'];
  $login->from_email = $_POST['email'];

  $login->add_message( $_POST['email'], 'Email');
  $login->add_message( $_POST['password'], 'Password');

  echo $login->send();
?>
