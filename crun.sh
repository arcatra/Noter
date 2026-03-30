#!/bin/bash

echo "Use 12(compile, run), 21(run, compile), for more flexibility"
printf "Choose a number:\n1. compile,\n2. run,\n3. exit\n(default: 2)\n[1/2/12] ? : "
read -r choice


# printf "\nArgs to be passed:\n"
# for arg in "$@"; do
#     echo "$arg"
#
# done

compile () {
    echo "Compiling only 1 DIR"
    javac -d ./app/bin/main/  ./app/src/main/java/noter/*.java
    printf "Done\n"
}

run() {
    echo ""
    java -cp ./app/bin/main/ noter.Noter "$@"
    echo ""
}

case "$choice" in
    12)
        compile
        run "$@"
    ;;
    21)
        run "$@"
        compile
    ;;
    1) 
        compile
    ;;
    2 | "") 
        run "$@"
    ;;
    3) 
        echo "Halting the process"
        echo ""

        exit
    ;;
    *)
    echo "Invalid input"
    ;;

esac


