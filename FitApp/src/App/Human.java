package App;

public class Human {
    public Sex sex;
    public int age;
    public int height;
    public int weight;
    public float pal;

    public Human(Sex sex, int age, int height, int weight, int palIndex){
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
        pal = getPalValue(palIndex);
    }

    public float GetPassiveEnergyIntake(){
        float newBMI = Mifflin();
        newBMI += HarrisBenedict();
        return newBMI/2;
    }

    public float GetTotalEnergyIntake(){
        return GetPassiveEnergyIntake()*pal;
    }

    public double GetBMI(){
        double bmi = weight/(Math.pow(height,2)/10000.0f);
        bmi = Math.round(bmi*100);
        return bmi/100;
    }


    private float Mifflin(){
        if(sex == Sex.MALE)
            return 10*weight+6.25f*height-5*age+5;
        else
            return 10*weight+6.25f*height-5*age-161;
    }

    private float HarrisBenedict(){
        if(sex == Sex.MALE)
            return 88.362f+13.397f*weight+4.799f*height-5.677f*age;
        else
            return 447.593f+9.247f*weight+3.098f*height-4.33f*age;
    }

    private float getPalValue(int index){
        return switch (index) {
            case 0 -> 1.25f;
            case 1 -> 1.4f;
            case 2 -> 1.6f;
            case 3 -> 1.7f;
            case 4 -> 2.0f;
            case 5 -> 2.3f;
            default -> 1;
        };
    }
}
