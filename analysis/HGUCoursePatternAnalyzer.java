package edu.handong.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVRecord;

import edu.handong.analysis.datamodel.Course;
import edu.handong.analysis.datamodel.Student;
import edu.handong.analysise.utils.NotEnoughArgumentException;
import edu.handong.analysise.utils.Utils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class HGUCoursePatternAnalyzer {

	private HashMap<String, Student> students;
	String input;
	String output;
	String analysis;
	String startyear;
	String endyear;
	String coursecode;
	boolean help;

	int numOfFiles = 0;
	/**
	 * This method runs our analysis logic to save the number courses taken by each student per semester in a result file.
	 * Run method must not be changed!!
	 * @param args
	 */
	public void run(String[] args) {
		
		Options options = createOptions();
	
		if(parseOptions(options, args)){
			if (help){
				printHelp(options);
				System.exit(0);
				return;
			}
			//input
			String dataPath = input;
			String resultPath = output;
			
			ArrayList<CSVRecord> lines = Utils.getLines(dataPath, true);
			students = loadStudentCourseRecords(lines, startyear, endyear);
			Map<String, Student> sortedStudents = new TreeMap<String,Student>(students); 
			//1번을 선택한경우
			if(analysis.equals("1")) {
				// Generate result lines to be saved.
				ArrayList<String> linesToBeSaved = countNumberOfCoursesTakenInEachSemester(sortedStudents);
				// Write a file (named like the value of resultPath) with linesTobeSaved.
				Utils.writeAFile(linesToBeSaved, resultPath);				
			}
			//2번을 선택한 경우
			else {
				//Year,Semester,CourseCode, CourseName,TotalStudents,StudentsTaken,Rate
				
				HashMap<String, Integer> result = new HashMap<String,Integer>();
				HashMap<String, Integer> totalresult = new HashMap<String,Integer>();
				
				ArrayList<String> linesToBeSaved = new ArrayList<String>();
				String courseName = null;
				
				for(CSVRecord temp : lines) {
					if(temp.get(4).equals(coursecode)) {
						courseName = temp.get(5);
						break;
					}
				}
				
				//모든 스튜던트들을 검사한다.
				for( String key : sortedStudents.keySet()) {
					Student tempStudent = sortedStudents.get(key);
					//스튜던트가 가지고 있는 모든 코스를 검사한다.
					for(Course tempCourse : tempStudent.getCoursesTaken()) {
						//임시 키값 세팅
						String tempString = tempCourse.getYearTaken() + "-" + tempCourse.getSemesterCourseTaken();
						//만약 코스코드가 원하는 코스코드와 같다면
						if(tempCourse.getCourseCode().equals(coursecode)) {
							if(result.isEmpty()) {
								result.put(tempString, 1);
							}
						else {
							if(result.containsKey(tempString)) {
								result.put(tempString, result.get(tempString)+1);
							}
							else {
								result.put(tempString, 1);
							}
						}
					}
				}
			}
				for( String resultKey : result.keySet()) {
					totalresult.put(resultKey, 0);
					for( String studentKey : sortedStudents.keySet()) {
						if(sortedStudents.get(studentKey).getSemestersByYearAndSemester().containsKey(resultKey)) {
							totalresult.put(resultKey, totalresult.get(resultKey)+1);
						}
					}
				}
				
				linesToBeSaved.add("Year,Semester,CourseCode, CourseName,TotalStudents,StudentsTaken,Rate");
				Map<String, Integer> finalResult = new TreeMap<String,Integer>(result);
				
				for(String resultkey : finalResult.keySet()) {
					
					float Rate = ((float)result.get(resultkey) / (float)totalresult.get(resultkey))*100; 
					
					String tempString = resultkey.split("-")[0] + "," 
										+resultkey.split("-")[1] + ","
										+coursecode + ","
										+courseName + ","
										+totalresult.get(resultkey) + ","
										+result.get(resultkey) + ","
										+String.format("%.1f", Rate) + "%";
					linesToBeSaved.add(tempString); 
				}
				Utils.writeAFile(linesToBeSaved, resultPath);
		}	
	}
}
		
	/**
	 * This method create HashMap<String,Student> from the data csv file. Key is a student id and the corresponding object is an instance of Student.
	 * The Student instance have all the Course instances taken by the student.
	 * @param lines
	 * @return
	 */
	private HashMap<String,Student> loadStudentCourseRecords(ArrayList<CSVRecord> lines, String startyear, String endyear){
		
		// TODO: Implement this method
		HashMap<String,Student> temp = new HashMap<String,Student>();
	
		for (CSVRecord line : lines) {
			//라인정보를 course로 바꿔줌
			Course tempCourse = new Course(line);
			Student tempStudent = new Student(tempCourse.getStudentId());
			tempStudent.addCourse(tempCourse);
			
			int start = Integer.parseInt(startyear);
			int end = Integer.parseInt(endyear);
			int tempyear = tempCourse.getYearTaken();
			
			//만약 처음으로 들어오는 값이면
			if(temp.isEmpty()&&tempyear>=start&&tempyear<=end) {
				temp.put(tempStudent.getStudentId(), tempStudent);
				//스튜던트에 course와 id를 셋해준뒤 그냥 넣어준다.
			} else {
				//만약 스튜던트 아이디가 이미 있다면
				if(temp.containsKey(tempStudent.getStudentId())&&tempyear>=start&&tempyear<=end) {
					//그 스튜던트에 코스를 추가해줍니다.
					temp.get(tempStudent.getStudentId()).addCourse(tempCourse);
				}else {
					if(tempyear>=start&&tempyear<=end)
						temp.put(tempStudent.getStudentId(), tempStudent);
				}
			}
        }
		return temp; // do not forget to return a proper variable.
	}

	/**
	 * This method generate the number of courses taken by a student in each semester. The result file look like this:
	 * StudentID, TotalNumberOfSemestersRegistered, Semester, NumCoursesTakenInTheSemester
	 * 0001,14,1,9
     * 0001,14,2,8
	 * ....
	 * 
	 * 0001,14,1,9 => this means, 0001 student registered 14 semeters in total. In the first semeter (1), the student took 9 courses.
	 * 
	 * 
	 * @param sortedStudents
	 * @return
	 */
	private ArrayList<String> countNumberOfCoursesTakenInEachSemester(Map<String, Student> sortedStudents) {
		
		// TODO: Implement this method
		//학생의 번호에 맞게 sorting되어 있는 sortedStudent
		//학번 - 총학기수 - N학기 - N학기 course count
		ArrayList<String> result = new ArrayList<String>();
		result.add("StudentID, TotalNumberOfSemestersRegistered, Semester, NumCoursesTakenInTheSemester");
		
		//스튜던트가 없을때까지 돌아갈 루프 -> 
		 for( String key : sortedStudents.keySet() ){
	         // key는 스튜던트 아이디 , 밸류튼 스튜던트   
			 String tempKey = key;
			 HashMap<String,Integer> tempHash = sortedStudents.get(key).loadStudentCourseRecords();
			 //총학기수는 어떻게 찾는가 젠장 -> loadStudentCourseRecords().size()이다!!!
			 String tempTotal = Integer.toString(tempHash.size());
			 //N학기..... -> loadStudentCourseRecords의 value값
			 //루프가 또 돌아가야한다.
			 //각 N번째 학기 -> loadStudentCourseRecords()의 사이즈만큼 돌아간다.
			 //loadStudentCourseRecords()의 밸류를 getNumCourseInNthSementer(int semester)에 넣는다!!!!!
			 //1 2 3 4 5 6 7 8 이렇게 흘러간다. secondkey = 스트링     value = 1234
			 
			 for(int i = 1; i <= tempHash.size(); i++) {
				 
				 for(String secondKey : tempHash.keySet()) {
					 if(i == tempHash.get(secondKey)) {
						 String tempSemester = Integer.toString(tempHash.get(secondKey));
						 String tempNumcourses = Integer.toString(sortedStudents.get(key).getNumCourseInNthSementer(i));
						 result.add(tempKey + "," + tempTotal + "," + tempSemester + "," + tempNumcourses);
					 }
				 } 
			 }
		 }
				
		return result; // do not forget to return a proper variable.
	}

	
	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			
			input = cmd.getOptionValue("i");
			output = cmd.getOptionValue("o");
			analysis = cmd.getOptionValue("a");
			startyear = cmd.getOptionValue("s");
			endyear = cmd.getOptionValue("e");
			coursecode = cmd.getOptionValue("c");
			help = cmd.hasOption("h");

		} catch (Exception e) {
			printHelp(options);
			return false;
		}

		return true;
	}

	// Definition Stage
	private Options createOptions() {
		Options options = new Options();
		
		options.addOption(Option.builder("i").longOpt("input")
				.desc("Set an input file path")
				.hasArg()
				.argName("Input path")
				.required()
				.build());
		
		options.addOption(Option.builder("o").longOpt("output")
				.desc("Set an output file path")
				.hasArg()
				.argName("Output path")
				.required()
				.build());
		
		options.addOption(Option.builder("a").longOpt("analysis")
				.desc("1: Count courses per semester, 2: Count per course name and year")
				.hasArg()
				.argName("Analysis option")
				.required()
				.build());
		
		options.addOption(Option.builder("c").longOpt("coursecode")
				.desc("Course code for '-a 2' option")
				.hasArg()
				.argName("course code")
				//.required()
				.build());
		
		options.addOption(Option.builder("s").longOpt("startyear")
				.desc("Set the start year for analysis e.g., -s 2002")
				.hasArg()
				.argName("Start year for analysis")
				.required()
				.build());
		
		options.addOption(Option.builder("e").longOpt("endyear")
				.desc("Set the end year for analysis e.g., -e 2005")
				.hasArg()
				.argName("End year for analysis")
				.required()
				.build());
		
		// add options by using OptionBuilder
		options.addOption(Option.builder("h").longOpt("help")
		        .desc("Help")
		        .build());

		return options;
	}
	
	private void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		String header = "HGU Course Analyzer";
		String footer ="";
		formatter.printHelp("HGU Course Analyzer", header, options, footer, true);
	}

}


