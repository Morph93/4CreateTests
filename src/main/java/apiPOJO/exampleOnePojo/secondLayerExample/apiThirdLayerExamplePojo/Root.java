package apiPOJO.exampleOnePojo.secondLayerExample.apiThirdLayerExamplePojo;


import java.util.ArrayList;
import java.util.List;

public class Root {

    private List<FirstDivisionOfRoot> firstDivisionOfRoot = new ArrayList<FirstDivisionOfRoot>();
    private SecondDivisionOfRoot secondDivisionOfRoot;

    public List<FirstDivisionOfRoot> getResult() {
        return firstDivisionOfRoot;
    }

    public void setResult(List<FirstDivisionOfRoot> firstDivisionOfRoot) {
        this.firstDivisionOfRoot = firstDivisionOfRoot;
    }

    public SecondDivisionOfRoot getPagination() {
        return secondDivisionOfRoot;
    }

    public void setPagination(SecondDivisionOfRoot secondDivisionOfRoot) {
        this.secondDivisionOfRoot = secondDivisionOfRoot;
    }


}