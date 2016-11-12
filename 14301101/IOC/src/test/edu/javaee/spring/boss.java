package test.edu.javaee.spring;

import dev.edu.javaee.spring.AC.Autowired;

public class boss {
  private office office;
  private car car;
  @Autowired
  public boss(car car ,office office){
      this.car = car;
      this.office = office ;
  }

  public String tostring(){
	  return "this boss has "+car.tostring()+"and in "+office.tostring();
  }
}
