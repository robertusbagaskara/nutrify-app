<?php
include_once('include.php');
$conn=getconnection();
if($conn==null){
    sendResponse(404, $conn, 'Server Unreachable');
}else{
    $sql="SELECT * FROM foodscan_data";
    $result=$conn->query($sql);
    if ($result->num_rows > 0) {
        $users=array();
        while($row=$result->fetch_assoc()){
            $user=array(
                "USER_ID"=>$row['USER_ID'],
                "FOOD_ID"=>$row['FOOD_ID'],
                "FOOD_NAME"=>$row['FOOD_NAME'],
                "NUTRITION"=>$row['NUTRITION'],
                "TIME"=>$row['TIME'],
            );
            array_push($users, $user);
        }
        sendResponse(300, $users, 'Nutrify Data');
    }else{
        sendResponse(500, [], 'Data Uploading is failed');
    }
    $conn->close();
}
?>