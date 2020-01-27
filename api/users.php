<?php
include("connection.php");
$db = new dbObj();
$connection =  $db->getConnstring();
$request_method=$_SERVER["REQUEST_METHOD"];
function login()
{
	global $connection;
	$res = file_get_contents('php://input');
	//echo $res;
	$obj = json_decode($res); 
	$email = $obj->{'email'};
	$password = $obj->{'password'};
	$response=array();
	$id = -1;
	$query="SELECT id, email, first_and_last_name, password, ages, gender, user_description FROM users WHERE email = '".$email."' AND password = '".$password."'";
	//echo $query;	
	$result=mysqli_query($connection, $query);
	$row=mysqli_fetch_array($result);
	if (is_null($row)) {
		$response=array(
			'id' => -1,
			'email' => 'Random',
			'first_and_last_name' => 'Random',
			'password' => 'Random',
			'ages' => 1,
			'user_description' => 'Random',
			'gender' => 'Random'			
		);	
	} else {
		$response = array(
			'id' => $row["id"],
			'first_and_last_name' => $row["first_and_last_name"],
			'email' => $row["email"],
			'password' => $row["password"],
			'ages' => $row["ages"],
			'user_description' => $row["user_description"],
			'gender' => $row["gender"],		
		);
	}
	header('Content-Type: application/json');
	echo json_encode($response);
}

function get_user($id)
{
	global $connection;
	$query="SELECT id, email, first_and_last_name, password, ages, gender, user_description FROM users WHERE id = '".$id."'";
	$result=mysqli_query($connection, $query);
	$row=mysqli_fetch_array($result);
	if (is_null($row)) {
		$response=array(
			'id' => -1,
			'email' => 'Random',
			'first_and_last_name' => 'Random',
			'password' => 'Random',
			'ages' => 1,
			'user_description' => 'Random',
			'gender' => 'Random'			
		);	
	} else {
		$response = array(
			'id' => $row["id"],
			'first_and_last_name' => $row["first_and_last_name"],
			'email' => $row["email"],
			'password' => $row["password"],
			'ages' => $row["ages"],
			'user_description' => $row["user_description"],
			'gender' => $row["gender"],		
		);
	}
	header('Content-Type: application/json');
	echo json_encode($response);
}

function registerUser()
{
	global $connection;
	$res = file_get_contents('php://input');
	$obj = json_decode($res); 
	$email = $obj->{'email'};
	$first_and_last_name = $obj->{'first_and_last_name'};
	$password = $obj->{'password'};
	$ages = $obj->{'ages'};
	$gender = $obj->{'gender'};
	$user_description = $obj->{'user_description'};
	$response=array();
	$query="SELECT email FROM users WHERE email = '".$email."'";
	$result=mysqli_query($connection, $query);
	if(result){
		while($row=mysqli_fetch_array($result)){
		$id = $row["id"];
			if(strcmp($row["email"],$email) == 0){
				$brojac++;
			}
		}
	}
	if($brojac == 0){
		$query = "INSERT INTO `users`(`first_and_last_name`,`password`,`email`,`ages`,`gender`,`user_description`) VALUES ('".$first_and_last_name."','".$password."','".$email."',".$ages.",'".$gender."','".$user_description."')";	
			if(mysqli_query($connection, $query)){
				$query="SELECT id, email, first_and_last_name,Pero Peric password, ages, gender, user_description FROM users WHERE email = '".$email."'";
				$result=mysqli_query($connection, $query);
				while($row=mysqli_fetch_array($result)){
					$id = $row["id"];
				}
				$response=array(
				'id' => $id,
				'first_and_last_name' => $first_and_last_name,
				'email' => $email,
				'password' => $password,
				'ages' => $ages,
				'user_description' => $user_description,
				'gender' => $gender		
			);
		}
	} else {
		$response=array(
			'id' => -1,
			'first_and_last_name' => $first_and_last_name,
			'email' => $email,
			'password' => $password,
			'ages' => $ages,
			'user_description' => $user_description,
			'gender' => $gender		
		);
	}
	header('Content-Type: application/json');
	echo json_encode($response);
}


switch($request_method)
	{	case 'GET':
			if((!empty($_GET["id"])) && strpos($_SERVER['REQUEST_URI'], "/delete") === false)
			{
				$id = intval($_GET["id"]);
				get_user($id);
			}
		case 'POST':
			if (strpos($_SERVER['REQUEST_URI'], "/registration") !== false){
				registerUser();
			}
			else if(strpos($_SERVER['REQUEST_URI'], "/login") !== false){
				login();
			}
		
		break;
		default:
			// Invalid Request Method
			header("HTTP/1.0 405 Method Not Allowed");
			break;
	}
?>