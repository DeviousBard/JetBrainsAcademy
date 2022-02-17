package main

import (
	"bufio"
	"fmt"
	"math"
	"os"
	"sort"
	"strconv"
	"strings"
)

type Applicant struct {
	FirstName             string
	LastName              string
	ExamScores            []float64
	SpecialExamScore      float64
	DepartmentPreferences []string
}

var applicants []Applicant
var deptApplicants = make(map[string][]Applicant)
var departments []string

type EmptyStruct = struct{}

func main() {
	var numAccepted, _ = strconv.Atoi(GetUserInput())
	LoadApplicantFile()
	for i := 0; i < 3; i++ {
		AssignApplicants(numAccepted, i)
	}
	SortDepartmentApplicants()
	PrintDepartmentAcceptances()
}

func LoadApplicantFile() {
	var deptSet = make(map[string]struct{})
	file, err := os.Open("applicants.txt")
	CheckForErrors(err)
	defer func(file *os.File) {
		CheckForErrors(file.Close())
	}(file)
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		var applicantStr = scanner.Text()
		var fields = strings.Fields(applicantStr)
		var physicsExam, _ = strconv.ParseFloat(fields[2], 64)
		var chemistryExam, _ = strconv.ParseFloat(fields[3], 64)
		var mathExam, _ = strconv.ParseFloat(fields[4], 64)
		var computerScienceExam, _ = strconv.ParseFloat(fields[5], 64)
		var specialExam, _ = strconv.ParseFloat(fields[6], 64)
		var applicant = Applicant{
			FirstName:             fields[0],
			LastName:              fields[1],
			ExamScores:            []float64{physicsExam, chemistryExam, mathExam, computerScienceExam},
			SpecialExamScore:      specialExam,
			DepartmentPreferences: []string{fields[7], fields[8], fields[9]},
		}
		applicants = append(applicants, applicant)
		deptSet[fields[7]] = EmptyStruct{}
		deptSet[fields[8]] = EmptyStruct{}
		deptSet[fields[9]] = EmptyStruct{}
	}
	departments = make([]string, len(deptSet))
	index := 0
	for key := range deptSet {
		departments[index] = key
		index++
	}
	sort.Slice(departments, func(i, j int) bool {
		if departments[i] != departments[j] {
			return departments[i] < departments[j]
		}
		return departments[i] > departments[j]
	})
}

func AssignApplicants(maxAccepted int, preferenceIndex int) {
	SortApplicantsByPreference(preferenceIndex)
	var index = 0
	for index < len(applicants) {
		var deptPref = applicants[index].DepartmentPreferences[preferenceIndex]
		var applicantsInDept = deptApplicants[deptPref]
		if len(applicantsInDept) < maxAccepted {
			applicantsInDept = append(applicantsInDept, applicants[index])
			deptApplicants[deptPref] = applicantsInDept
			applicants = append(applicants[:index], applicants[index+1:]...)
		} else {
			index++
		}
	}
}

func (applicant Applicant) GetBestExamScoreByDepartment(department string) float64 {
	var departmentExams = map[string][]int{"Physics": {0, 2}, "Chemistry": {1}, "Mathematics": {2}, "Engineering": {3, 2}, "Biotech": {1, 0}}
	var exams = departmentExams[department]
	var sum float64 = 0
	for i := 0; i < len(exams); i++ {
		sum += applicant.ExamScores[exams[i]]
	}
	var deptAverage = sum / float64(len(exams))
	return math.Max(deptAverage, applicant.SpecialExamScore)
}

func SortApplicantsByPreference(preferenceNum int) {
	sort.Slice(applicants, func(i, j int) bool {
		if applicants[i].DepartmentPreferences[preferenceNum] == applicants[j].DepartmentPreferences[preferenceNum] {
			var dept = applicants[i].DepartmentPreferences[preferenceNum]
			var averageExamScore1 = applicants[i].GetBestExamScoreByDepartment(dept)
			var averageExamScore2 = applicants[j].GetBestExamScoreByDepartment(dept)
			if averageExamScore1 == averageExamScore2 {
				var name1 = applicants[i].FirstName + " " + applicants[i].LastName
				var name2 = applicants[j].FirstName + " " + applicants[i].LastName
				return name1 < name2
			} else {
				return averageExamScore1 > averageExamScore2
			}
		}
		return applicants[i].DepartmentPreferences[preferenceNum] < applicants[j].DepartmentPreferences[preferenceNum]
	})
}

func SortDepartmentApplicants() {
	for index := range departments {
		var acceptedApplicants = deptApplicants[departments[index]]
		sort.Slice(acceptedApplicants, func(i, j int) bool {
			var examScore1 = acceptedApplicants[i].GetBestExamScoreByDepartment(departments[index])
			var examScore2 = acceptedApplicants[j].GetBestExamScoreByDepartment(departments[index])
			if examScore1 == examScore2 {
				var name1 = acceptedApplicants[i].FirstName + " " + acceptedApplicants[i].LastName
				var name2 = acceptedApplicants[j].FirstName + " " + acceptedApplicants[i].LastName
				return name1 < name2
			} else {
				return examScore1 > examScore2
			}
		})
		deptApplicants[departments[index]] = acceptedApplicants
	}
}

func PrintDepartmentAcceptances() {
	for index := range departments {
		ExportToAcceptedApplicantsToFile(departments[index])
		fmt.Println(departments[index])
		var applicantsInDept = deptApplicants[departments[index]]
		for i := 0; i < len(applicantsInDept); i++ {
			fmt.Printf("%s %s %.1f\n", applicantsInDept[i].FirstName, applicantsInDept[i].LastName, applicantsInDept[i].GetBestExamScoreByDepartment(departments[index]))
		}
		fmt.Println()
	}
}

func ExportToAcceptedApplicantsToFile(dept string) {
	var fileName = strings.ToLower(dept) + ".txt"
	file, err := os.Create(fileName)
	CheckForErrors(err)
	defer func(file *os.File) {
		CheckForErrors(file.Close())
	}(file)
	var acceptedApplicants = deptApplicants[dept]
	for i := 0; i < len(acceptedApplicants); i++ {
		var entry = fmt.Sprintf("%s %s %.1f\n", acceptedApplicants[i].FirstName, acceptedApplicants[i].LastName, acceptedApplicants[i].GetBestExamScoreByDepartment(dept))
		_, err := file.WriteString(entry)
		CheckForErrors(err)
	}
	CheckForErrors(file.Sync())
}

func GetUserInput() string {
	var response string
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	response = scanner.Text()
	return response
}

func CheckForErrors(e error) {
	if e != nil {
		panic(e)
	}
}
