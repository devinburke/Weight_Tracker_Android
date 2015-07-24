<?php
$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
require_once 'include/DB_Functions.php';
$func =  new DB_Functions; //creates a new function class
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // gets the tag posted in HashMap Params
    $tag = $_POST['tag'];
    // json response array being sent back from API
    $response = array("tag" => $tag, "error" => FALSE);
    // check for tag type
    if ($tag == 'login') {
        // Request type is check Login
        $email = $_POST['email'];
        $password = $_POST['password'];
        $user = $func->getUserByEmailAndPassword($email, $password);
        if ($user != false) {  //sees if user is returned
            $response["error"] = FALSE;
            $response["uid"] = $user["uid"]; //puts user information into response array
            $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["created_at"] = $user["created_at"];
            $response["user"]["updated_at"] = $user["updated_at"];
			$uid = $user["uid"];
			$stored_no = "no"; 
			$stored_yes = "yes";//below checks if user has set up initial data
			if($func->check_initial($uid)){$response ["user"]["stored"] = $stored_yes;} 
			else{$response ["user"]["stored"] = $stored_no;}
			echo json_encode($response);
        } else {
            // user not found
            $response["error"] = TRUE;
            $response["error_msg"] = "Incorrect email or password!";
            echo json_encode($response);
        }
    } else if ($tag == 'register') {
        // Request type is Register new user
        $name = $_POST['name'];
        $email = $_POST['email'];
        $password = $_POST['password'];
        // check if user is already existed
        if ($func->isUserExisted($email)) {
            // user is already existed - error response
            $response["error"] = TRUE;
            $response["error_msg"] = "User already existed";
            echo json_encode($response);
        } else {
            // store user
            $user = $func->storeUser($name, $email, $password);
            if ($user) {
                // user stored successfully
                $response["error"] = FALSE;
                $response["uid"] = $user["uid"];
                $response["user"]["name"] = $user["name"];
                $response["user"]["email"] = $user["email"];
                $response["user"]["created_at"] = $user["created_at"];
                $response["user"]["updated_at"] = $user["updated_at"];
                echo json_encode($response);
            } else {
                // user failed to store
                $response["error"] = TRUE;
                $response["error_msg"] = "Error occured in Registartion";
                echo json_encode($response);
            }
        }
    } 
	else if ($tag == 'weight') { //tag for storing user weight data
		$uid = $_POST["uid"];
		$weight = $_POST["weight"];
		$weightgoal = $_POST["weightgoal"];
		$stored = $_POST["initial"];
		$user = $func->storeWeight($uid, $weight, $weightgoal); //stores user entered weight data
		if ($user == true){
			$response["error"] = FALSE;
			$store = $func->store_initial_check($uid, $stored);
			echo json_encode($response);
		} else{
			//user weight failed to store
			$response ["error"] = TRUE;
			$response ["error_msg"] = "Error occured in storing user weight data";
						echo json_encode($response);
			}
		}	
	
	else if($tag == 'home'){
		$uid = $_POST["uid"];
		$user = $func->getWeight($uid); 
		$start = $func->getstartweight($uid);//gets the user weight data
		if ($user AND $start){
			$response ["error"] = FALSE;
			$response ["user"]["weight"] = $user["weight_new"];
			$response ["user"]["weightgoal"] = $user["goal_weight"];
			$response ["user"]["startweight"] = $start["weight_new"];
			echo json_encode($response);
		} else{ //error should not happen, but meant to check if user had entered data
			$response["error"] = TRUE;
			$response ["error_msg"] = "error retrieving data";
			echo json_encode($response);
			}
		}
		else if ($tag == 'main_check'){ //checks if user has already entered main data, redirects to home page
			$uid = $_POST["uid"];
			$stored_no = "no";
			$stored_yes = "yes";
			if($func->check_initial($uid)){
				$response ["user"]["stored"] = $stored_yes;
			}else {
				$response ["user"]["stored"] = $stored_no;
			}
			echo json_encode($response);
		}
		else if ($tag == 'newweight'){
			$uid = $_POST["uid"];
			$newweight = $_POST["newweight"]; 
			$days = $func->getday($uid);
			$day = $days["daycounter"];
			$goal_weight = $days["goal_weight"];
			$daycounter = $day + 1;
			$update = $func->updateWeight($uid);
			if ($func->storeNewWeight($daycounter, $uid, $newweight, $goal_weight)){
					$response ["error"] = False;
					$response ["error_msg"] = "no error";
			} else{
				$response["error"] = True;
				$response["error_msg"] = "error storing new weight data";
			}
			echo json_encode($response);
		}
		else if ($tag == 'graph'){
			
			$uid = $_POST["uid"];
			$sql = 	"Select weight_new, daycounter FROM weight_progress where uid = '$uid' order by daycounter";
			$result = mysqli_query($con, $sql);
			$num_rows = mysqli_num_rows($result);
			$count = 1;
			if ($num_rows>0){
				while($row = $result->fetch_assoc()){
					$response[$count] = $row["weight_new"];
					$count = $count + 1;
				}
				$response ["error"] = False;
					$response ["error_msg"] = "no error";
					$response ["row"] = $num_rows;
				
			}else{
				
				$response["error"] = True;
				$response["error_msg"] = "error storing new weight data";
				
			}
			echo json_encode($response);
		
		}
			
			
	else {
        // user failed to store
        $response["error"] = TRUE;
        $response["error_msg"] = "Unknow 'tag' value. It should be either 'login' or 'register'";
        echo json_encode($response);
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter 'tag' is missing!";
    echo json_encode($response);
}
?>