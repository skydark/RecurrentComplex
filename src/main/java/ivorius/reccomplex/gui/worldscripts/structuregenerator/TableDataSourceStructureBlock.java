/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.gui.worldscripts.structuregenerator;

import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.reccomplex.blocks.TileEntityStructureGenerator;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.TableDirections;
import ivorius.reccomplex.gui.table.*;
import ivorius.reccomplex.scripts.world.WorldScriptStructureGenerator;
import ivorius.reccomplex.structures.StructureRegistry;
import ivorius.ivtoolkit.blocks.Directions;
import joptsimple.internal.Strings;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static ivorius.reccomplex.gui.table.TableCellEnum.Option;

/**
 * Created by lukas on 05.06.14.
 */
public class TableDataSourceStructureBlock extends TableDataSourceSegmented implements TableCellPropertyListener
{
    protected WorldScriptStructureGenerator script;

    protected TableNavigator tableNavigator;
    protected TableDelegate tableDelegate;

    public TableDataSourceStructureBlock(WorldScriptStructureGenerator script, TableNavigator tableNavigator, TableDelegate tableDelegate)
    {
        this.script = script;
        this.tableNavigator = tableNavigator;
        this.tableDelegate = tableDelegate;
    }

    private static boolean doAllStructuresExist(Iterable<String> structures)
    {
        for (String s : structures)
        {
            if (s.length() != 0 && StructureRegistry.INSTANCE.getStructure(s) == null)
                return false; // s==0 = "No structure"
        }

        return true;
    }

    @Override
    public int numberOfSegments()
    {
        return script.isSimpleMode() ? 4 : 4;
    }

    @Override
    public int sizeOfSegment(int segment)
    {
        if (segment == 0)
            return 1;
        else if (segment == 1)
            return 1;
        else if (segment == 2)
            return 3;
        else if (segment == 3)
            return script.isSimpleMode() ? 2 : 1;

        return 0;
    }

    @Override
    public TableElement elementForIndexInSegment(GuiTable table, int index, int segment)
    {
        if (segment == 0)
        {
            TableCellBoolean cell = new TableCellBoolean("simpleMode", script.isSimpleMode());
            cell.addPropertyListener(this);
            return new TableElementCell("Simple Mode", cell);
        }
        else if (segment == 1)
        {
            if (script.isSimpleMode())
            {
                TableCellString cell = new TableCellString("generators", Strings.join(script.getStructureNames(), ","));
                cell.setShowsValidityState(true);
                cell.setValidityState(doAllStructuresExist(script.getStructureNames()) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.SEMI_VALID);
                cell.addPropertyListener(this);
                return new TableElementCell("Generators (A,B,...)", cell);
            }
            else
            {
                TableCellString cell = new TableCellString("listID", script.getStructureListID());
                cell.addPropertyListener(this);
                return new TableElementCell("List ID", cell);
            }
        }
        else if (segment == 2)
        {
            if (index == 0)
            {
                TableCellInteger cell = new TableCellInteger("xShift", script.getStructureShift().x, -50, 50);
                cell.addPropertyListener(this);
                return new TableElementCell("Shift: X", cell);
            }
            else if (index == 1)
            {
                TableCellInteger cell = new TableCellInteger("yShift", script.getStructureShift().y, -50, 50);
                cell.addPropertyListener(this);
                return new TableElementCell("Shift: Y", cell);
            }
            else if (index == 2)
            {
                TableCellInteger cell = new TableCellInteger("zShift", script.getStructureShift().z, -50, 50);
                cell.addPropertyListener(this);
                return new TableElementCell("Shift: Z", cell);
            }
        }
        else if (segment == 3)
        {
            if (script.isSimpleMode())
            {
                if (index == 0)
                {
                    TableCellEnum cell = new TableCellEnum<>("rotation", script.getStructureRotation(),
                            new Option<>(0, "0 Clockwise"), new Option<>(1, "1 Clockwise"), new Option<>(2, "2 Clockwise"), new Option<>(3, "3 Clockwise"), new Option<Integer>(null, "Random (if rotatable)"));
                    cell.addPropertyListener(this);
                    return new TableElementCell("Rotation", cell);
                }
                else if (index == 1)
                {
                    TableCellEnum cell = new TableCellEnum<>("mirror", script.getStructureMirror(),
                            new Option<>(false, "false"), new Option<>(true, "true"), new Option<Boolean>(null, "Random (if mirrorable)"));
                    cell.addPropertyListener(this);
                    return new TableElementCell("Mirror", cell);
                }
            }
            else
            {
                TableCellEnum cell = new TableCellEnum<>("front", script.getFront(), TableDirections.getDirectionOptions(ArrayUtils.add(Directions.HORIZONTAL, null), "random"));
                cell.addPropertyListener(this);
                return new TableElementCell("Front", cell);
            }
        }

        return null;
    }

    @Override
    public void valueChanged(TableCellPropertyDefault cell)
    {
        if (cell.getID() != null)
        {
            switch (cell.getID())
            {
                case "simpleMode":
                    script.setSimpleMode((Boolean) cell.getPropertyValue());
                    tableDelegate.reloadData();
                    break;
                case "generators":
                {
                    String value = ((String) cell.getPropertyValue());
                    script.setStructureNames(Arrays.asList(value.split(",")));
                    ((TableCellString) cell).setValidityState(doAllStructuresExist(script.getStructureNames()) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.SEMI_VALID);
                    break;
                }
                case "listID":
                {
                    script.setStructureListID((String) cell.getPropertyValue());
                    break;
                }
                case "xShift":
                {
                    BlockCoord shift = script.getStructureShift();
                    script.setStructureShift(new BlockCoord((int) cell.getPropertyValue(), shift.y, shift.z));
                    break;
                }
                case "yShift":
                {
                    BlockCoord shift = script.getStructureShift();
                    script.setStructureShift(new BlockCoord(shift.x, (int) cell.getPropertyValue(), shift.z));
                    break;
                }
                case "zShift":
                {
                    BlockCoord shift = script.getStructureShift();
                    script.setStructureShift(new BlockCoord(shift.x, shift.y, (int) cell.getPropertyValue()));
                    break;
                }
                case "rotation":
                {
                    script.setStructureRotation((Integer) cell.getPropertyValue());
                    break;
                }
                case "mirror":
                {
                    script.setStructureMirror((Boolean) cell.getPropertyValue());
                    break;
                }
                case "front":
                {
                    script.setFront((ForgeDirection) cell.getPropertyValue());
                    break;
                }
            }
        }
    }
}
