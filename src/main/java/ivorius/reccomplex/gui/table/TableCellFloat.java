/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.gui.table;

import ivorius.ivtoolkit.gui.GuiControlListener;
import ivorius.ivtoolkit.gui.GuiSlider;
import ivorius.reccomplex.utils.scale.Scale;
import ivorius.reccomplex.utils.scale.Scales;

/**
 * Created by lukas on 02.06.14.
 */
public class TableCellFloat extends TableCellPropertyDefault<Float> implements GuiControlListener<GuiSlider>
{
    protected GuiSlider slider;

    protected boolean enabled = true;
    protected float min;
    protected float max;
    protected Scale scale = Scales.none();

    protected String titleFormat = "%.4f";

    public TableCellFloat(String id, float value, float min, float max)
    {
        super(id, value);

        this.min = min;
        this.max = max;
    }

    public float getMin()
    {
        return min;
    }

    public void setMin(float min)
    {
        this.min = min;
    }

    public float getMax()
    {
        return max;
    }

    public void setMax(float max)
    {
        this.max = max;
    }

    public Scale getScale()
    {
        return scale;
    }

    public void setScale(Scale scale)
    {
        this.scale = scale;
    }

    public String getTitleFormat()
    {
        return titleFormat;
    }

    public void setTitleFormat(String titleFormat)
    {
        this.titleFormat = titleFormat;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;

        if (slider != null)
            slider.enabled = enabled;
    }

    @Override
    public void initGui(GuiTable screen)
    {
        super.initGui(screen);

        Bounds bounds = bounds();
        slider = new GuiSlider(-1, bounds.getMinX(), bounds.getMinY() + (bounds.getHeight() - 20) / 2, bounds.getWidth(), 20, String.format(titleFormat, property));
        slider.setMinValue(scale.out(min));
        slider.setMaxValue(scale.out(max));
        slider.enabled = enabled;
        slider.addListener(this);

        slider.setValue(scale.out(property));
        slider.visible = !isHidden();

        screen.addButton(this, 0, slider);
    }

    @Override
    public void setHidden(boolean hidden)
    {
        super.setHidden(hidden);

        if (slider != null)
            slider.visible = !hidden;
    }

    @Override
    public void valueChanged(GuiSlider gui)
    {
        property = scale.in(gui.getValue());
        gui.displayString = String.format(titleFormat, property);

        alertListenersOfChange();
    }

    @Override
    public void setPropertyValue(Float value)
    {
        super.setPropertyValue(value);

        if (slider != null)
        {
            slider.setValue(scale.out(property));
            slider.displayString = String.format(titleFormat, property);
        }
    }
}
