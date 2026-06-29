#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char *argv[]) {
  // 1. Define the core executable components for the JVM
  char *java_bin = "java";
  char *native_access = "--enable-native-access=ALL-UNNAMED";
  char *classpath_flag = "-cp";

  // Relative paths pointing to your compiled Java class files and external
  // SQLite jars
  char *classpath = "./app/bin/main:./app/lib/*";
  char *main_class = "noter.Noter";

  // 2. Calculate necessary buffer size for the argument array
  // fixed_args_count represents the 5 elements defined above (indices 0 through
  // 4)
  int fixed_args_count = 5;
  int total_args_count = fixed_args_count + argc;

  // Allocate the exact amount of block memory needed for our string pointers
  char **new_argv = malloc(total_args_count * sizeof(char *));
  if (new_argv == NULL) {
    perror("Failed to allocate memory for argument forwarding");
    return 1;
  }

  // 3. Populate the fixed positions to instruct the JVM configuration
  new_argv[0] = java_bin;
  new_argv[1] = native_access;
  new_argv[2] = classpath_flag;
  new_argv[3] = classpath;
  new_argv[4] = main_class;

  // 4. Forward all user arguments passed into this binary right into the
  // payload array We skip argv[0] (the C binary name) and map the rest
  // sequentially into new_argv
  for (int i = 1; i < argc; i++) {
    new_argv[fixed_args_count + i - 1] = argv[i];
  }

  // 5. Explicitly NULL-terminate the array array so execvp knows where the
  // boundary ends
  new_argv[total_args_count - 1] = NULL;

  // 6. In-place transformation: Transmute this running C process directly into
  // the JVM
  execvp(java_bin, new_argv);

  // If execvp executes successfully, the process space is completely replaced.
  // Reaching this code means an error occurred
  perror("Execution failed. Please verify that Java is installed and in your "
         "system PATH.");
  free(new_argv);
  return 1;
}
