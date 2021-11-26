package autotest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 测试用例集, 单例模式
 */
public class TestSet implements Iterable<TestCase> {

    public static final String ROOT_PATH = "testfile/testfiles";

    private final List<TestCase> testSet = new ArrayList<>();

    public int size() {
        return testSet.size();
    }

    @Override
    public Iterator<TestCase> iterator() {
        return testSet.iterator();
    }

    private static class InstanceHolder {
        private static final TestSet instance = new TestSet();
    }

    public static TestSet getInstance() {
        return InstanceHolder.instance;
    }

    private TestSet() {
        // C
        testSet.add(new TestCase("C-1", "C/testfile1.txt", "C/input1.txt", "C/output1.txt"));
        testSet.add(new TestCase("C-2", "C/testfile2.txt", "C/input2.txt", "C/output2.txt"));
        testSet.add(new TestCase("C-3", "C/testfile3.txt", "C/input3.txt", "C/output3.txt"));
        testSet.add(new TestCase("C-4", "C/testfile4.txt", "C/input4.txt", "C/output4.txt"));
        testSet.add(new TestCase("C-5", "C/testfile5.txt", "C/input5.txt", "C/output5.txt"));
        testSet.add(new TestCase("C-6", "C/testfile6.txt", "C/input6.txt", "C/output6.txt"));
        testSet.add(new TestCase("C-7", "C/testfile7.txt", "C/input7.txt", "C/output7.txt"));
        testSet.add(new TestCase("C-8", "C/testfile8.txt", "C/input8.txt", "C/output8.txt"));
        testSet.add(new TestCase("C-9", "C/testfile9.txt", "C/input9.txt", "C/output9.txt"));
        testSet.add(new TestCase("C-10", "C/testfile10.txt", "C/input10.txt", "C/output10.txt"));
        testSet.add(new TestCase("C-11", "C/testfile11.txt", "C/input11.txt", "C/output11.txt"));
        testSet.add(new TestCase("C-12", "C/testfile12.txt", "C/input12.txt", "C/output12.txt"));
        testSet.add(new TestCase("C-13", "C/testfile13.txt", "C/input13.txt", "C/output13.txt"));
        testSet.add(new TestCase("C-14", "C/testfile14.txt", "C/input14.txt", "C/output14.txt"));
        testSet.add(new TestCase("C-15", "C/testfile15.txt", "C/input15.txt", "C/output15.txt"));
        testSet.add(new TestCase("C-16", "C/testfile16.txt", "C/input16.txt", "C/output16.txt"));
        testSet.add(new TestCase("C-17", "C/testfile17.txt", "C/input17.txt", "C/output17.txt"));
        testSet.add(new TestCase("C-18", "C/testfile18.txt", "C/input18.txt", "C/output18.txt"));
        testSet.add(new TestCase("C-19", "C/testfile19.txt", "C/input19.txt", "C/output19.txt"));
        testSet.add(new TestCase("C-20", "C/testfile20.txt", "C/input20.txt", "C/output20.txt"));
        testSet.add(new TestCase("C-21", "C/testfile21.txt", "C/input21.txt", "C/output21.txt"));
        testSet.add(new TestCase("C-22", "C/testfile22.txt", "C/input22.txt", "C/output22.txt"));
        testSet.add(new TestCase("C-23", "C/testfile23.txt", "C/input23.txt", "C/output23.txt"));
        testSet.add(new TestCase("C-24", "C/testfile24.txt", "C/input24.txt", "C/output24.txt"));
        testSet.add(new TestCase("C-25", "C/testfile25.txt", "C/input25.txt", "C/output25.txt"));
        testSet.add(new TestCase("C-26", "C/testfile26.txt", "C/input26.txt", "C/output26.txt"));
        testSet.add(new TestCase("C-27", "C/testfile27.txt", "C/input27.txt", "C/output27.txt"));
        testSet.add(new TestCase("C-28", "C/testfile28.txt", "C/input28.txt", "C/output28.txt"));
        testSet.add(new TestCase("C-29", "C/testfile29.txt", "C/input29.txt", "C/output29.txt"));
        // B
        testSet.add(new TestCase("B-1", "B/testfile1.txt", "B/input1.txt", "B/output1.txt"));
        testSet.add(new TestCase("B-2", "B/testfile2.txt", "B/input2.txt", "B/output2.txt"));
        testSet.add(new TestCase("B-3", "B/testfile3.txt", "B/input3.txt", "B/output3.txt"));
        testSet.add(new TestCase("B-4", "B/testfile4.txt", "B/input4.txt", "B/output4.txt"));
        testSet.add(new TestCase("B-5", "B/testfile5.txt", "B/input5.txt", "B/output5.txt"));
        testSet.add(new TestCase("B-6", "B/testfile6.txt", "B/input6.txt", "B/output6.txt"));
        testSet.add(new TestCase("B-7", "B/testfile7.txt", "B/input7.txt", "B/output7.txt"));
        testSet.add(new TestCase("B-8", "B/testfile8.txt", "B/input8.txt", "B/output8.txt"));
        testSet.add(new TestCase("B-9", "B/testfile9.txt", "B/input9.txt", "B/output9.txt"));
        testSet.add(new TestCase("B-10", "B/testfile10.txt", "B/input10.txt", "B/output10.txt"));
        testSet.add(new TestCase("B-11", "B/testfile11.txt", "B/input11.txt", "B/output11.txt"));
        testSet.add(new TestCase("B-12", "B/testfile12.txt", "B/input12.txt", "B/output12.txt"));
        testSet.add(new TestCase("B-13", "B/testfile13.txt", "B/input13.txt", "B/output13.txt"));
        testSet.add(new TestCase("B-14", "B/testfile14.txt", "B/input14.txt", "B/output14.txt"));
        testSet.add(new TestCase("B-15", "B/testfile15.txt", "B/input15.txt", "B/output15.txt"));
        testSet.add(new TestCase("B-16", "B/testfile16.txt", "B/input16.txt", "B/output16.txt"));
        testSet.add(new TestCase("B-17", "B/testfile17.txt", "B/input17.txt", "B/output17.txt"));
        testSet.add(new TestCase("B-18", "B/testfile18.txt", "B/input18.txt", "B/output18.txt"));
        testSet.add(new TestCase("B-19", "B/testfile19.txt", "B/input19.txt", "B/output19.txt"));
        testSet.add(new TestCase("B-20", "B/testfile20.txt", "B/input20.txt", "B/output20.txt"));
        testSet.add(new TestCase("B-21", "B/testfile21.txt", "B/input21.txt", "B/output21.txt"));
        testSet.add(new TestCase("B-22", "B/testfile22.txt", "B/input22.txt", "B/output22.txt"));
        testSet.add(new TestCase("B-23", "B/testfile23.txt", "B/input23.txt", "B/output23.txt"));
        testSet.add(new TestCase("B-24", "B/testfile24.txt", "B/input24.txt", "B/output24.txt"));
        testSet.add(new TestCase("B-25", "B/testfile25.txt", "B/input25.txt", "B/output25.txt"));
        testSet.add(new TestCase("B-26", "B/testfile26.txt", "B/input26.txt", "B/output26.txt"));
        testSet.add(new TestCase("B-27", "B/testfile27.txt", "B/input27.txt", "B/output27.txt"));
        // A
        testSet.add(new TestCase("A-1", "A/testfile1.txt", "A/input1.txt", "A/output1.txt"));
        testSet.add(new TestCase("A-2", "A/testfile2.txt", "A/input2.txt", "A/output2.txt"));
        testSet.add(new TestCase("A-3", "A/testfile3.txt", "A/input3.txt", "A/output3.txt"));
        testSet.add(new TestCase("A-4", "A/testfile4.txt", "A/input4.txt", "A/output4.txt"));
        testSet.add(new TestCase("A-5", "A/testfile5.txt", "A/input5.txt", "A/output5.txt"));
        testSet.add(new TestCase("A-6", "A/testfile6.txt", "A/input6.txt", "A/output6.txt"));
        testSet.add(new TestCase("A-7", "A/testfile7.txt", "A/input7.txt", "A/output7.txt"));
        testSet.add(new TestCase("A-8", "A/testfile8.txt", "A/input8.txt", "A/output8.txt"));
        testSet.add(new TestCase("A-9", "A/testfile9.txt", "A/input9.txt", "A/output9.txt"));
        testSet.add(new TestCase("A-10", "A/testfile10.txt", "A/input10.txt", "A/output10.txt"));
        testSet.add(new TestCase("A-11", "A/testfile11.txt", "A/input11.txt", "A/output11.txt"));
        testSet.add(new TestCase("A-12", "A/testfile12.txt", "A/input12.txt", "A/output12.txt"));
        testSet.add(new TestCase("A-13", "A/testfile13.txt", "A/input13.txt", "A/output13.txt"));
        testSet.add(new TestCase("A-14", "A/testfile14.txt", "A/input14.txt", "A/output14.txt"));
        testSet.add(new TestCase("A-15", "A/testfile15.txt", "A/input15.txt", "A/output15.txt"));
        testSet.add(new TestCase("A-16", "A/testfile16.txt", "A/input16.txt", "A/output16.txt"));
        testSet.add(new TestCase("A-17", "A/testfile17.txt", "A/input17.txt", "A/output17.txt"));
        testSet.add(new TestCase("A-18", "A/testfile18.txt", "A/input18.txt", "A/output18.txt"));
        testSet.add(new TestCase("A-19", "A/testfile19.txt", "A/input19.txt", "A/output19.txt"));
        testSet.add(new TestCase("A-20", "A/testfile20.txt", "A/input20.txt", "A/output20.txt"));
        testSet.add(new TestCase("A-21", "A/testfile21.txt", "A/input21.txt", "A/output21.txt"));
        testSet.add(new TestCase("A-22", "A/testfile22.txt", "A/input22.txt", "A/output22.txt"));
        testSet.add(new TestCase("A-23", "A/testfile23.txt", "A/input23.txt", "A/output23.txt"));
        testSet.add(new TestCase("A-24", "A/testfile24.txt", "A/input24.txt", "A/output24.txt"));
        testSet.add(new TestCase("A-25", "A/testfile25.txt", "A/input25.txt", "A/output25.txt"));
        testSet.add(new TestCase("A-26", "A/testfile26.txt", "A/input26.txt", "A/output26.txt"));
        // Others
    }

}
