<?php
 
class DB_Functions {


public function storeWeight ($uid, $weight, $weightgoal){ //stores user weight data into weight table
$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
$day = 1;
$weight_current = 1;
$result = mysqli_query($con, "INSERT INTO weight_progress (daycounter, weight_new, weight_current, goal_weight, uid)
 VALUES ('$day', '$weight', '$weight_current', '$weightgoal', '$uid')");
	if ($result){
		return true;
		}
		else {
			return false;
		}	
	}
	
public function storeNewWeight($day, $uid, $newweight, $goal_weight){
	$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
	$weight_current = 1;
	$sql = "
	INSERT INTO weight_progress (daycounter, weight_new, weight_current, goal_weight, uid) VALUES
	('$day', '$newweight', '$weight_current', '$goal_weight', '$uid')";
	$result = mysqli_query($con, $sql);
	if ($result){
		return true;
		}
		else {
			return false;
		}
}
	
public function storeUser($name, $email, $password) {
	$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
    $uuid = uniqid('', true);
    $hash = $this->hashSSHA($password);
    $encrypted_password = $hash["encrypted"]; // encrypted password
    $salt = $hash["salt"]; // salt
    $result = mysqli_query($con, "INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES('$uuid', '$name', '$email', '$encrypted_password', '$salt', NOW())");
    // check for successful store
    if ($result) {
    // get user details 
    $uid = mysqli_insert_id($con); // last inserted id
    $result = mysqli_query($con, "SELECT * FROM users WHERE uid = $uid");
    // return user details
    return mysqli_fetch_array($result);
		} else {
			return false;
			}
		}
		
public function store_initial_check($uid, $stored){ //stores initial data, which means the user has filled out initial data forms
	$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
	$sql = "INSERT INTO initial_detials (uid, stored) VALUES ('$uid','$stored') ";
	$result = mysqli_query($con, $sql);
	}
	
public function getWeight($uid){ //gets the weight data from weight table
$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
 	$sql = "Select weight_new, goal_weight FROM weight_progress WHERE uid = '$uid' AND weight_current = 1";
	$result = mysqli_query($con, $sql);
	$no_of_rows = mysqli_num_rows($result);
	if ($no_of_rows>0){
		return mysqli_fetch_array($result);
			}else {return false;}
		}
public function getstartweight($uid){
	$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
	$sql = "Select weight_new from weight_progress WHERE daycounter = 1";
	$result = mysqli_query($con, $sql);
		$no_of_rows = mysqli_num_rows($result);
if ($no_of_rows>0){
		return mysqli_fetch_array($result);
			}else {return false;}
}

public function getday($uid){
		$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");

$sql = "Select daycounter, goal_weight FROM weight_progress WHERE weight_current = 1";
$result = mysqli_query($con, $sql);
$no_of_rows = mysqli_num_rows($result);
if ($no_of_rows>0){
		return mysqli_fetch_array($result);
			}else {return false;}
}

public function updateWeight($uid){
			$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
$sql = "
UPDATE weight_progress
SET weight_current = 0
WHERE weight_current = 1 AND uid = '$uid'";
$result = mysqli_query($con, $sql);
}
		
public function check_initial($uid){
$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");	//checks to see if initial data has been logged or not
$sql = "Select * from initial_detials WHERE uid = '$uid'";
$result = mysqli_query($con, $sql);
$num_rows = mysqli_num_rows($result);
	if ($num_rows>0){
		return true;
			}else return false;
	}

	
	
	
public function getUserByEmailAndPassword($email, $password) {
	$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
	
    $result = mysqli_query($con, "SELECT * FROM users WHERE email = '$email'") or die (mysqli_error($con));
    // check for result 
    $no_of_rows = mysqli_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysqli_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $result;
            }
        } else {
            // user not found
            return false;
        }
    }

public function isUserExisted($email) {
	$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");

    $result = mysqli_query($con, "SELECT email from users WHERE email = '$email'");
    $no_of_rows = mysqli_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
public function hashSSHA($password) {
	$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
    $salt = sha1(rand());
    $salt = substr($salt, 0, 10);
    $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
    $hash = array("salt" => $salt, "encrypted" => $encrypted);
    return $hash;
    }
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
		$host = "localhost";
$user = "dburke";
$dbpassword = "";
$database = "dburke_android_api";
$con = mysqli_connect("$host", "$user", "$dbpassword", "$database");
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
        return $hash;
    }
}
 
?>