<?php
function getconnection()
{
    $conn=new mysqli("localhost","root", "", "db_foodscan");
    if($conn->connect_error){
        $conn=null;        
    }
    return $conn;    
}