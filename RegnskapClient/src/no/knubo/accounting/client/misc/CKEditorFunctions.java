package no.knubo.accounting.client.misc;

public class CKEditorFunctions {

    public static native void configStylesInt(String styles, String id)
    /*-{
       $wnd['CKEDITOR'].stylesSet.add( id, eval("["+styles+"]"));
       $wnd['CKEDITOR'].config.stylesSet = id;
    
       $wnd['CKEDITOR'].config.toolbar_MyToolbar =
    [
        { name: 'column1', items : [ 'NewPage','Preview' ] },
        { name: 'column2', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
        { name: 'column3', items : [ 'Find','Replace','-','SelectAll','-','Scayt' ] },
                '/',
        { name: 'styles', items : [ 'Styles','Format' ] },
        { name: 'basicstyles', items : [ 'Bold','Italic','Strike','-','RemoveFormat' ] },
        { name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote' ] },
        { name: 'links', items : [ 'Link','Unlink','Anchor' ] },
        { name: 'tools', items : [ 'Maximize','-','About' ] }
    ];
    $wnd['CKEDITOR'].config.toolbar = "MyToolbar";
    
    }-*/;


}
