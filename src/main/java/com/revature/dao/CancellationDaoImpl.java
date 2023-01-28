package com.revature.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.revature.config.DatabaseConnection;
import com.revature.constants.Constants;
import com.revature.model.Appointment;
import com.revature.app.menu.MenuDrivenApplication;
import com.revature.util.DateTimeUtil;

public class CancellationDaoImpl implements CancellationDao {
	static  Logger logger = Logger.getLogger(CancellationDaoImpl.class);
	static Connection con = DatabaseConnection.getConnection();
	MenuDrivenApplication menu = new MenuDrivenApplication();
	public static int cancellationId = 10001;
	Appointment appointment = new Appointment();
	public static int appointmentCount =0;
	public static boolean checkTest= false;
	public void displayAppointmentlist() throws ClassNotFoundException {
		Connection con=null;
		try {
			Class.forName("com.mysql.cj.jdbc.Drive");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pams_project", "root", "2798");
			java.sql.Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(Constants.SELECT_APPOINTMENT_QUERY);
			System.out.println();
			System.out.println(
					"APPOINTMENT ID \t LOGIN ID\t  DOCTOR ID \t DOCTOR NAME\t patient_age \t TOTAL PRICE\t appointment DATE");
			System.out.println();
			while (rs.next()) {
				String appointmentId = rs.getString("appointment_id");
				String prefferedLoginId = rs.getString("preferred_login_id");
				String doctorId = rs.getString("doctor_id");
				String doctorName = rs.getString("doctor_name");
				String patient_age = rs.getString("patient_age");
				String totalFee = rs.getString("total_fee");
				String appointmentDate = rs.getString("appointment_date");
				System.out.println(appointmentId + "\t\t" + prefferedLoginId + "\t\t" + doctorId + "\t\t" + doctorName
						+ "\t\t" + patient_age + "\t\t" + totalFee + "\t\t" + appointmentDate);
			}
			String GET_APPOINTMENT_DETAILS = "SELECT * FROM APPOINTMENT_DETAILS";
			ResultSet o = stm.executeQuery(GET_APPOINTMENT_DETAILS);
			while (rs.next()) {
				Appointment appointment = new Appointment(rs.getInt("appointment_id"), rs.getInt("doctor_id"), rs.getInt("patient_age"),
						rs.getDouble("total_fee"), rs.getString("appointment_date"), rs.getString("status"),
						rs.getInt("cancellation_id"), rs.getString("cancellation_date"),
						rs.getString("cancellation_reason"), rs.getString("preferred_login_id"));
				CancellationDao.appointmentlist.add(appointment);
				appointmentCount++;
			}
			//System.out.println(CancellationDao.appointmentlist);

		} catch (SQLException e) {
			//System.out.println(e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
		}
	}
	
	

	@Override
	public void checkAppointment(int oId) {

		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Drive");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pams_project", "root", "2798");
			Statement stm = con.createStatement();
			String GET_APPOINTMENT_DETAILS = "SELECT * FROM APPOINTMENT_DETAILS";
			ResultSet rs = stm.executeQuery(GET_APPOINTMENT_DETAILS);

			while (rs.next()) {
				Appointment appointment = new Appointment(rs.getInt("appointment_id"), rs.getInt("doctor_id"), rs.getInt("patient_age"),
						rs.getDouble("total_fee"), rs.getString("appointment_date"), rs.getString("status"),
						rs.getInt("cancellation_id"), rs.getString("cancellation_date"),
						rs.getString("cancellation_reason"), rs.getString("preferred_login_id"));
				CancellationDao.appointmentlist.add(appointment);
			}
			boolean flag = false;
			for (Appointment i : CancellationDao.appointmentlist) {
				if (i.getAppointmentId() == oId) {
					flag = true;
					checkTest=true;
					if (i.getStatus().equals("Cancelled")) {
						System.out.println("appointment already Cancelled..");
						menu.cancelAppointments();
					}
				}
			}
			if (!flag) {
				System.out.println("No appointment found with appointment Id " + oId +" . Try with valid appointment Id:(");
				menu.cancelAppointments();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
		}
		try {
			con.close();
		}catch(SQLException e) {
	    }
	}
	
	@Override
	public void cancelAppointment(int oId, String reason) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pams_project", "root", "2798");

			String updateQuery = Constants.UPDATE_STATUS;
			PreparedStatement ps = con.prepareStatement(updateQuery);

			ps.setInt(1, generateCancellationId());
			ps.setString(2, DateTimeUtil.getCurrentDateTime());
			ps.setString(3, reason);
			ps.setInt(4, oId);
			int n = ps.executeUpdate();
			ps.close();
			String getPid = "SELECT DOCTOR_ID FROM APPOINTMENT_DETAILS WHERE APPOINTMENT_ID ="+ String.valueOf(oId);
			Statement stmt=con.createStatement();
			
			ResultSet rs=stmt.executeQuery(getPid);
			int pId = 0;
			while(rs.next()) {
				pId=rs.getInt("doctor_id");
			}
			logger.info(pId);
			reflectChanges(pId);

		} catch (ClassNotFoundException | SQLException e) {
			logger.info(e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
		}
	}


	@Override
	public int generateCancellationId() {

		return cancellationId++;
	}

	@Override
	public void displayCancellationDetails(int appointmentId) {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Drive");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pams_project", "root", "2798");
			java.sql.Statement stm = con.createStatement();
			String GET_CANCEL_appointment = "SELECT * FROM Appointment_DETAILS WHERE appointment_id=?";
			PreparedStatement ps = con.prepareStatement(GET_CANCEL_appointment);
			ps.setInt(1, appointmentId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Appointment appointment = new Appointment(rs.getInt("appointment_id"), rs.getInt("doctor_id"), rs.getInt("patient_age"),
						rs.getDouble("total_fee"), rs.getString("appointment_date"), rs.getString("status"),
						rs.getInt("cancellation_id"), rs.getString("cancellation_date"),
						rs.getString("cancellation_reason"), rs.getString("preferred_login_id"));
				CancellationDao.cancelList.add(appointment);
			}
			System.out.println(CancellationDao.cancelList);

		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}

	@Override
	public void reflectChanges(int doctorId) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pams_project", "root", "2798");

			String updateQuery = Constants.UPDATE_COUNT;
			PreparedStatement ps = con.prepareStatement(updateQuery);
			ps.setInt(1, doctorId);
			int n = ps.executeUpdate();

		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
		}
		try {

			con.close();
		} catch (SQLException e) {
		}

	}

}

	