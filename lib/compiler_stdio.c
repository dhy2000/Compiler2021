// Implement of `getint` and `printf` function.

#include "compiler_stdio.h"

#include <stdlib.h>
#include <unistd.h>
#include <stdarg.h>

#define EOF (-1)

inline static char sys_getc() {
    char val = 0;
    int r = read(STDIN_FILENO, &val, 1);
    if (r < 0) {
        write(STDERR_FILENO, "! Error reading stdin !\n", 24);
        exit(-1);
    } else if (r == 0) {
        return -1;
    }
    return val;
}

int getint() {
    char c = sys_getc();
    int ret = 0;
    int neg = 0;
    while ((c != EOF) && !(c >= '0' && c <= '9')) {
        if (c == '-') neg = 1 - neg;
        c = sys_getc();
    }
    if (c == EOF) {
        write(STDERR_FILENO, "! Unexpected EOF !\n", 19);
        return EOF;
    }
    while ((c >= '0' && c <= '9')) {
        ret = (ret << 3) + (ret << 1) + (c ^ '0');
        c = sys_getc();
    }
    return neg ? -ret : ret;
}

inline static void sys_putn(int n) {
    if (n == -2147483648) {
        write(STDOUT_FILENO, "-2147483648", 11);
        return;
    }
    else {
        int absn = (n < 0) ? (-n) : (n);
        char stk[12];
        int siz = 0;
        if (absn == 0) {
            stk[0] = '0';
            siz = 1;
        } else {
            while (absn) {
                stk[siz] = (absn % 10) + '0';
                absn /= 10;
                siz++;
            }
        }
        if (n < 0)
            write(STDOUT_FILENO, "-", 1);
        while (siz--) {
            write(STDOUT_FILENO, &stk[siz], 1);
        }
    }
}

void printf(const char *fmt, ...) {
    // Supports `%d` only.
    va_list ap;
    va_start(ap, fmt);

    const char *p = fmt;
    for (; *p != 0; p++) {
        if (*p == '%') {
            p++;
            if (*p == '%') { // %%
                write(STDOUT_FILENO, "%", 1);
            } else if (*p == 'd') {
                int nextn = va_arg(ap, int);
                sys_putn(nextn);
            }
        } else {
            write(STDOUT_FILENO, p, 1);
        }
    }
    va_end(ap);
    // return 0;
}