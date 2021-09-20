TestFile=
TestIn=
TestOut=

IncludePath=include
LibPath=lib
LibSource=$(wildcard $(LibPath)/*.c)

RedirectIn=
RedirectOut=

ifneq ($(TestIn),)
	RedirectIn= < $(TestIn)
endif

ifneq ($(TestOut),)
	RedirectOut= > $(TestOut)
endif

.PHONY: init compile run clean testall

all: init compile run clean

init:
	echo "#include \"compiler_stdio.h\"" > test.c
	cat $(TestFile) >> test.c
	echo $(LibSource)

compile:
	gcc -o test -I $(IncludePath) test.c $(LibSource)

run:
	./test $(RedirectIn) $(RedirectOut)

clean:
	rm ./test*

testall:
	script/testall.sh

zip:
	script/zipall.sh

cleanzip:
	rm *.zip -f
