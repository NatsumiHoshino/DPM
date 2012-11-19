package master;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class TwoWheeledRobot {
	
	// Sensor ports belonging to the master
	private LightSensor masterLightSensor = new LightSensor(SensorPort.S1);
	private UltrasonicSensor sideUltrasonicSensor = new UltrasonicSensor(SensorPort.S2);
	private UltrasonicSensor frontUltrasonicSensor = new UltrasonicSensor(SensorPort.S3);
	
	public static final double DEFAULT_LEFT_RADIUS = 2.05;	//2.75
	public static final double DEFAULT_RIGHT_RADIUS = 2.05;	//2.75
	public static final double DEFAULT_WIDTH = 18.48;	//15.8	//21.40
	
	// Wheel constants when the robot has a load.
	public static final double LOAD_LEFT_RADIUS = 0;
	public static final double LOAD_RIGHT_RADIUS = 0;
	public static final double LOAD_WIDTH = 0;
	
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	private boolean isTurning = false;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   double width,
						   double leftRadius,
						   double rightRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
		this.leftMotor.setAcceleration(150);
		this.rightMotor.setAcceleration(150);
	}

	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(leftMotor, rightMotor, DEFAULT_WIDTH, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width) {
		this(leftMotor, rightMotor, width, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	// accessors
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius +
				rightMotor.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius -
				rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftMotor.setSpeed(900);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightMotor.setSpeed(900);
		else
			rightMotor.setSpeed((int)rightSpeed);
	}
	
	// Additional methods
	public void rotate(int angle)
	{
		leftMotor.rotate(convertAngle(DEFAULT_LEFT_RADIUS, DEFAULT_WIDTH, angle), true);
		rightMotor.rotate(-convertAngle(DEFAULT_RIGHT_RADIUS, DEFAULT_WIDTH, angle));
	}
	public void rotateIndependently(int angle)
	{
		leftMotor.rotate(convertAngle(DEFAULT_LEFT_RADIUS, DEFAULT_WIDTH, angle), true);
		rightMotor.rotate(-convertAngle(DEFAULT_RIGHT_RADIUS, DEFAULT_WIDTH, angle), true);
	}
	public void goForward(double distance)
	{
		leftMotor.rotate(-convertDistance(DEFAULT_LEFT_RADIUS, distance), true);
		rightMotor.rotate(-convertDistance(DEFAULT_RIGHT_RADIUS,distance));
	}
	public void goForward()
	{
		leftMotor.backward();
		rightMotor.backward();
	}
	
	public void stop(){
		leftMotor.stop(true); //don't wait for engine to stop
		rightMotor.stop(); //this makes sure both motors stop at the same time
	}
	
	public void start(){
		leftMotor.forward();
		rightMotor.forward();
	}
	//Helper methods from SquareDriver
	// TODO: we might want to place these in a separate class
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public boolean isTurning(){
		return this.isTurning;
	}

	/**
	 * Get the front Ultrasonic Sensor.
	 * @return an UltrasonicSensor object.
	 */
	public UltrasonicSensor getFrontUltrasonicSensor() {
		return frontUltrasonicSensor;
	}
	
	/**
	 * Get the side Ultrasonic Sensor.
	 * @return an UltrasonicSensor object.
	 */
	public UltrasonicSensor getSideUltrasonicSensor() {
		return sideUltrasonicSensor;
	}
	
	/**
	 * Get the Light Sensor of the master.
	 * @return an LightSensor object.
	 */
	public LightSensor getMasterLightSensor() {
		return masterLightSensor;
	}
}
