#!/bin/sh

echo "Use 12(compile, run), 21(run, compile), for more flexibility"
printf "Choose a number:\n1. compile,\n2. run,\n3. exit\n(default: 2)\n[1/2] ? : "
read -r choice

root=./app/src/main/java/
bin=./app/bin/main/

compile () {
    subDirCount=0

    for dir in "$root"*/; do
        if [ -d "$dir" ]; then
            subDirCount=$((subDirCount + 1)) 
        fi

    done

    echo "Compiling $subDirCount DIR(s)"
    find "$root" -name "*.java" | xargs javac -d "$bin" || exit 1
    printf "Done\n"
}

run() {
    echo
    java -cp "$bin" noter.Noter "$@" || exit 1
    echo
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
        echo 

        exit
    ;;
    *)
    echo "Invalid input"
    ;;

esac


