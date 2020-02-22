set -e

if [ -z "$1" ]; then
  echo "Usage: $0 <homework id>"
  exit 1;
fi

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )";
hw="hw$1"
uid="100000000"
hw_dir="$script_dir/$hw/"
solution_dir="$hw_dir/$uid/"
project_dir="$script_dir/../"
testcases_dir="$hw_dir/testcases"
grade_output="/tmp/grade.out"

# move to the homework dir to create the tar
cd $solution_dir

# tar the contents
tar cvf $uid.tar *.java

# move the tar into the project root
# TODO trap and remove
mv $uid.tar $project_dir

# switch the working directory to the root of the project
cd $project_dir

# move the test cases to the root of the project
cp -r $testcases_dir .

# run the grading script
bash grade.sh $hw . testcases $uid.tar &>$grade_output

function results {
  cat $grade_output | grep "Testing.*.java"
}

function check {
  if [ $1 -gt 0 ]; then
    echo "Exited with error status, failed test"
    cat $2
  fi
}

set +e
echo "Checking grad, expecting 51, got: "
cat $grade_output | grep "$uid 51"
check $? $grade_output
echo "Checking failed test, expecting 1, got: "
cat $grade_output | grep "WARN: Failed.*Missing.java" | wc -l | grep 1
check $? $grade_output
echo "Checking extra rows, expecting 1, got: "
cat results/$uid/extra | wc -l | grep 1
check $? $grade_output
set -e

rm "$project_dir/$uid.tar"
rm -r "$project_dir/testcases"
