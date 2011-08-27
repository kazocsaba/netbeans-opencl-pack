/*
 * Created on 15. March 2007, 16:10
 */

package com.mbien.opencl.editor.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;

/**
 * @author Michael Bien
 */
public class CompilerAnnotations {
 
 private final static HashMap<DataObject, ArrayList<CompilerAnnotation>> annotationMap = new HashMap<>();
 

    private CompilerAnnotations() {
    }
    
    public static void addAnnotation(DataObject dao, CompilerAnnotation.AnnotationType type, String msg, int lineNumber) {
        
        Line.Set lines = dao.getCookie(LineCookie.class).getLineSet();

        if(lineNumber < 1 || lineNumber >= lines.getLines().size())
            return;
                
        ArrayList<CompilerAnnotation> annotations;
        if(!annotationMap.containsKey(dao)) {
            annotations = new ArrayList<>();
            annotationMap.put(dao, annotations);
        }else{
            annotations = annotationMap.get(dao);
        }
        
        Line line;
        try {
            line = lines.getCurrent(lineNumber-1);
        } catch (IndexOutOfBoundsException ex) {
            // the document has been changed and the line is deleted
            return;
        }
        
        String text = line.getText();
        
        if (text == null) 
            return; // document is already closed
        
        char[] chars = text.toCharArray();
        int start;
        int end;
        for(start = 0; start < chars.length; start++)
            if(!Character.isWhitespace(chars[start]))
                break;
        
        for(end = chars.length-1; end > start; end--)
            if(!Character.isWhitespace(chars[end-1]))
                break;
        
        CompilerAnnotation annotation = new CompilerAnnotation(type, msg);
        annotation.attach(line.createPart(start, end-start));
        annotations.add(annotation);
        
    }
    
    public static void clearAll() {
        for (ArrayList<CompilerAnnotation> list : annotationMap.values()) 
            for (CompilerAnnotation annotation : list) 
                annotation.detach();
        annotationMap.clear();
    }
    
    public static void removeAnnotations(DataObject dao) {
        ArrayList<CompilerAnnotation> annotations = annotationMap.remove(dao);
        if(annotations != null)
            for (CompilerAnnotation compilerAnnotation : annotations)
                compilerAnnotation.detach();
    }


}
