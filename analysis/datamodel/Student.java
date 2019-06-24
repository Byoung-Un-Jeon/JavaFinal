package edu.handong.analysis.datamodel;
import java.util.ArrayList;
import java.util.HashMap;

public class Student {
	private String studentId;
	private ArrayList<Course> coursesTaken;
	private HashMap<String,Integer> semestersByYearAndSemester;
	
	public String getStudentId() {
		return studentId;
	}

	public HashMap<String, Integer> getSemestersByYearAndSemester() {
		
		semestersByYearAndSemester = new HashMap<String,Integer>();
		int count = 1;
		
		for(Course tempCourse : coursesTaken) {
			String temp =  (Integer.toString(tempCourse.getYearTaken())+"-"+Integer.toString(tempCourse.getSemesterCourseTaken())).trim();
			//tempHash.putIfAbsent(temp, count++);
			
			if(semestersByYearAndSemester.isEmpty()) {
				semestersByYearAndSemester.put(temp, count);
				count++;
			}
			else {
				if(semestersByYearAndSemester.containsKey(temp))
					continue;
				else {
					semestersByYearAndSemester.put(temp, count);
					count++;
				}
			}
		}
		return semestersByYearAndSemester;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public ArrayList<Course> getCoursesTaken() {
		return coursesTaken;
	}

	public Student(String studentId) {
		this.studentId = studentId;
		this.coursesTaken = new ArrayList<Course>();
		this.semestersByYearAndSemester = new HashMap<String,Integer>();
	} // constructor
	
	
	public void addCourse(Course newRecord) {
		coursesTaken.add(newRecord);
	}
	
	public HashMap<String,Integer> loadStudentCourseRecords(){
		//어레이리스트에서  yearTaken과  semesterTaken을 합쳐 String으로 저장한다 -> key
		
		HashMap<String,Integer> tempHash = new HashMap<String,Integer>();
		int count = 1;
		
		for(Course tempCourse : coursesTaken) {
			String temp =  (Integer.toString(tempCourse.getYearTaken())+"-"+Integer.toString(tempCourse.getSemesterCourseTaken())).trim();
			//tempHash.putIfAbsent(temp, count++);
			
			if(tempHash.isEmpty()) {
				tempHash.put(temp, count);
				count++;
			}
			else {
				if(tempHash.containsKey(temp))
					continue;
				else {
					tempHash.put(temp, count);
					count++;
				}
			}
		}
		return tempHash;
	}
	
	public int getNumCourseInNthSementer(int semester) {
		int count=0;
		//semestersByYearAndSemester에서 해당하는 semester를 찾는다
		for(Course tempCourse : coursesTaken) {
			String courseInfo = Integer.toString(tempCourse.getYearTaken()) + "-" + Integer.toString(tempCourse.getSemesterCourseTaken());
			if(semester == getSemestersByYearAndSemester().get(courseInfo)) {
				count++;
			}
		}
		//count를 늘려준다.
		return count;
		
	}
}
