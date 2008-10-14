package ca.sqlpower.architect.swingui;

import java.awt.Color;
import java.awt.Point;
import java.sql.Types;

import junit.framework.TestCase;
import ca.sqlpower.architect.ArchitectException;
import ca.sqlpower.architect.SQLColumn;
import ca.sqlpower.architect.SQLDatabase;
import ca.sqlpower.architect.SQLRelationship;
import ca.sqlpower.architect.SQLTable;
import ca.sqlpower.architect.swingui.event.SelectionEvent;

public class TestRelationship extends TestCase {

	Relationship rel;
	PlayPen pp;
    TablePane tp1;
    TablePane tp2;
	
	protected void setUp() throws Exception {
		super.setUp();
		pp = ArchitectFrame.getMainInstance().getProject().getPlayPen();
		SQLTable t1 = new SQLTable(pp.getDatabase(), true);
        t1.addColumn(new SQLColumn(t1, "pkcol_1", Types.INTEGER, 10,0));
        t1.addColumn(new SQLColumn(t1, "fkcol_1", Types.INTEGER, 10,0));
        t1.getColumnByName("pkcol_1").setPrimaryKeySeq(0);

		pp.getDatabase().addChild(t1);
		pp.addTablePane(tp1 = new TablePane(t1, pp), new Point(0,0));
		SQLTable t2 = new SQLTable(pp.getDatabase(), true);
        t2.addColumn(new SQLColumn(t2, "col_1", Types.INTEGER, 10,0));
        t2.addColumn(new SQLColumn(t2, "fkcol", Types.INTEGER, 10,0));      

		pp.getDatabase().addChild(t2);
		pp.addTablePane(tp2 = new TablePane(t2, pp), new Point(0,0));
		SQLRelationship sqlrel = new SQLRelationship();
		sqlrel.attachRelationship(t1, t2, false);
        sqlrel.addMapping(t1.getColumnByName("pkcol_1"), 
                t2.getColumnByName("fkcol"));
		rel = new Relationship(pp, sqlrel);
	}
	
	public void testCopyConstructor() {
		PlayPen newpp = new PlayPen(pp.getDatabase());
		Relationship rel2 = new Relationship(rel, newpp.getContentPane(), null, null);
		assertNotSame("The new relationship component has the same UI delegate as the original", rel.getUI(), rel2.getUI());
	}

    public void testHighlightWithRelationshipTypeChange() throws ArchitectException {               
        rel.setSelected(true,SelectionEvent.SINGLE_SELECT);
        assertEquals(Color.RED,tp1.getColumnHighlight(0));
        assertEquals(Color.RED,tp2.getColumnHighlight(1));
        assertEquals(tp2.getForeground(), tp2.getColumnHighlight(0));
        rel.setSelected(false,SelectionEvent.SINGLE_SELECT);
        
        assertEquals(tp1.getForeground(), tp1.getColumnHighlight(0));
        assertEquals(tp2.getForeground(), tp2.getColumnHighlight(1));
        assertEquals(tp2.getForeground(), tp2.getColumnHighlight(0));
        
        rel.setSelected(true,SelectionEvent.SINGLE_SELECT);
        rel.getModel().setIdentifying(true);       
        
        assertEquals(Color.RED,tp1.getColumnHighlight(0));
        SQLColumn fkCol = tp2.getModel().getColumnByName("fkcol");
        assertEquals(0, tp2.getModel().getColumnIndex(fkCol));
        assertEquals(Color.RED,tp2.getColumnHighlight(0));
        assertEquals(tp2.getForeground(), tp2.getColumnHighlight(1));      
    }
    
    private void setupRefCountTests(SQLDatabase db, SQLTable pkTable, SQLTable fkTable, SQLRelationship sourceRel) throws ArchitectException {
        pkTable.setName("pkTable");
        pkTable.addColumn(new SQLColumn(pkTable, "PKTableCol1", Types.INTEGER, 1, 0));
        pkTable.addColumn(new SQLColumn(pkTable, "PKTableCol2", Types.INTEGER, 1, 0));
        pkTable.getColumn(0).setPrimaryKeySeq(0);
        db.addChild(pkTable);
        
        fkTable.setName("child");
        fkTable.addColumn(new SQLColumn(fkTable, "FKTableCol1", Types.INTEGER, 1, 0));
        db.addChild(fkTable);
        
        sourceRel.addMapping(pkTable.getColumn(0), fkTable.getColumn(0));
        sourceRel.attachRelationship(pkTable, fkTable, true);        
    }
    
    public void testRefCountWithFkTableInsertedFirst() throws ArchitectException {
        SQLDatabase db = new SQLDatabase();        
        SQLTable fkTable = new SQLTable(db, true);
        SQLRelationship sourceRel = new SQLRelationship();
        SQLTable pkTable = new SQLTable(db, true);        
        setupRefCountTests(db,pkTable, fkTable, sourceRel);
        
        TablePane FkPane = pp.importTableCopy(fkTable, new Point(10, 10));
        TablePane PkPane = pp.importTableCopy(pkTable, new Point(10, 10));
                
        assertEquals(2,FkPane.getModel().getColumn(0).getReferenceCount());
        assertEquals(1, PkPane.getModel().getColumn(0).getReferenceCount());    
    }

    public void testRefCountWithPkTableInsertedFirst() throws ArchitectException {
        SQLDatabase db = new SQLDatabase();
        SQLTable fkTable = new SQLTable(db, true);
        SQLRelationship sourceRel = new SQLRelationship();
        SQLTable pkTable = new SQLTable(db, true);        
        setupRefCountTests(db, pkTable, fkTable, sourceRel);
        
        TablePane PkPane = pp.importTableCopy(pkTable, new Point(10, 10));        
        TablePane FkPane = pp.importTableCopy(fkTable, new Point(10, 10));

        assertEquals(2, FkPane.getModel().getColumn(0).getReferenceCount());
        assertEquals(1, PkPane.getModel().getColumn(0).getReferenceCount());    
    }
    
    public void testRefCountWithMultipleTablesInserted() throws ArchitectException{
        SQLDatabase db = new SQLDatabase();
        SQLTable fkTable = new SQLTable(db, true);
        SQLRelationship sourceRel = new SQLRelationship();
        SQLTable pkTable = new SQLTable(db, true);        
        
        setupRefCountTests(db, pkTable, fkTable, sourceRel);
        SQLTable fkTable2 = new SQLTable (db,true);
        fkTable2.addColumn(new SQLColumn(fkTable2, "FKTable2Col1", Types.INTEGER, 1, 0));
        SQLRelationship newRel = new SQLRelationship();
        newRel.addMapping(pkTable.getColumn(0), fkTable2.getColumn(0));
        newRel.attachRelationship(pkTable,fkTable2, true);
               
        
        TablePane PkPane = pp.importTableCopy(pkTable, new Point(10, 10));        
        TablePane FkPane = pp.importTableCopy(fkTable, new Point(10, 10));
        TablePane FkPane2 = pp.importTableCopy(fkTable2, new Point(10, 10));               
        
        assertEquals (1, PkPane.getModel().getColumn(0).getReferenceCount());
        assertEquals (2, FkPane.getModel().getColumn(0).getReferenceCount());
        assertEquals (2, FkPane2.getModel().getColumn(0).getReferenceCount());        
    }

}