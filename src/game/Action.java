/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author Pavel
 */
public class Action {
    
    String name;
    int from, to;
    
    public Action(String name, int from, int to){
        this.name = name;
        this.from = from;
        this.to = to;
    }
}
