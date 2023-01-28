package com.revature.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



import com.revature.model.Appointment;

public interface CancellationDao {
	public static List<Appointment> cancelList = new ArrayList<>();
	public void reflectChanges(int doctorId);
	public void displayCancellationDetails(int appointmentId);
	public static ArrayList<Appointment> appointmentlist = new ArrayList<>();
	public void checkAppointment(int oId);
	public void cancelAppointment(int appointmentId, String reason) throws SQLException;
	public void displayAppointmentlist() throws ClassNotFoundException;
	public int generateCancellationId();
}