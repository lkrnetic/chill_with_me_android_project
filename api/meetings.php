<?php
include("connection.php");
$db = new dbObj();
$connection =  $db->getConnstring();
$request_method=$_SERVER["REQUEST_METHOD"];

function get_meetings() {
	global $connection;
	$query="SELECT * FROM meetings";
	$meetings = array();
	$result = mysqli_query($connection, $query);
	while($row=mysqli_fetch_array($result)) {
		$meetings[] = array("title" => $row["title"], "user_id" => $row["user_id"], "user_first_and_last_name" => $row["user_first_and_last_name"], "latitude" => $row["latitude"], "longitude" => $row["longitude"], "address_name" => $row["address_name"], "event_description" => $row["event_description"], "event_time" => $row["event_time"]);
	}
	$response[] = array("meetings" => $meetings); 
	header('Content-Type: application/json');
	echo json_encode($response);
}

function delete_meeting($meeting_id)
{
	global $connection;
	$query="DELETE FROM meetings WHERE id = '".$meeting_id."'";
	if (mysqli_query($connection, $query)) {
		$response[] = array('user_id' => 1,
			'title' =>"Random",
			'latitude' =>123.233,
			'longitude' =>123.233,
			'address_name' =>"Random",
			'event_description' =>"Random",
			'event_time' =>"Random",
			'user_first_and_last_name' =>"Random");
	} else {
		$response[] = array('user_id' => -1,
			'user_id' => 1,
			'title' =>"Random",
			'latitude' =>123.233,
			'longitude' =>123.233,
			'address_name' =>"Random",
			'event_description' =>"Random",
			'event_time' =>"Random",
			'user_first_and_last_name' =>"Random");
	}
	header('Content-Type: application/json');
	echo json_encode($response);
}

function insert_meeting()
{
	global $connection;
	$res = file_get_contents('php://input');
	//echo $res;
	$obj = json_decode($res); 
	$user_id = $obj->{'user_id'};
	$title = $obj->{'title'};
	$latitude = $obj->{'latitude'};
	$longitude = $obj->{'longitude'};
	$address_name = $obj->{'address_name'};
	$event_description = $obj->{'event_description'};
	$event_time = $obj->{'event_time'};
	$user_first_and_last_name = $obj->{'user_first_and_last_name'};
	$query = "INSERT INTO `meetings`(`user_id`, `title`, `latitude`, `longitude`, `address_name`, `event_description`, `event_time`, `user_first_and_last_name`) VALUES ('".$user_id."','".$title."','".$latitude."','".$longitude."','".$address_name."','".$event_description."','".$event_time."','".$user_first_and_last_name."')";
	if(mysqli_query($connection, $query)){
		$response = array(
			'user_id' => $user_id,
			'title' =>$title,
			'latitude' =>$latitude,
			'longitude' =>$longitude,
			'address_name' =>$address_name,
			'event_description' =>$event_description,
			'event_time' =>$event_time,
			'user_first_and_last_name' =>$user_first_and_last_name
		);
	}
	header('Content-Type: application/json');
	echo json_encode($response);
	
}

switch($request_method)
	{
		case 'GET':
			// Retrive questions
			if (strpos($_SERVER['REQUEST_URI'], "/meetings") !== false){
				get_meetings();
			}
			else if((!empty($_GET["id"])) && strpos($_SERVER['REQUEST_URI'], "/delete") === false)
			{
				$id = intval($_GET["id"]);
				get_question($id);
			}
			break;
		case 'POST':
			if (strpos($_SERVER['REQUEST_URI'], "/meeting") !== false){
				insert_meeting();
			}
		break;
		default:
			// Invalid Request Method
			header("HTTP/1.0 405 Method Not Allowed");
			break;
	}
?>