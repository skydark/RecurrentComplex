/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.structures.generic.gentypes;

import com.google.gson.*;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceMazeGenerationInfo;
import ivorius.reccomplex.gui.table.TableDataSource;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.structures.generic.maze.ConnectorStrategy;
import ivorius.reccomplex.structures.generic.maze.SavedMazeComponent;
import ivorius.reccomplex.structures.generic.Selection;
import ivorius.reccomplex.structures.generic.maze.SavedMazePath;
import net.minecraft.util.StatCollector;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * Created by lukas on 07.10.14.
 */
public class MazeGenerationInfo extends StructureGenerationInfo
{
    private static Gson gson = createGson();

    public String id = "";

    public String mazeID;
    public SavedMazeComponent mazeComponent;

    public MazeGenerationInfo()
    {
        this("MazeGen1", "", new SavedMazeComponent(null, ConnectorStrategy.DEFAULT_WALL));
        mazeComponent.rooms.addAll(Selection.zeroSelection(3));
    }

    public MazeGenerationInfo(String id, String mazeID, SavedMazeComponent mazeComponent)
    {
        this.id = id;
        this.mazeID = mazeID;
        this.mazeComponent = mazeComponent;
    }

    public static Gson createGson()
    {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(MazeGenerationInfo.class, new Serializer());
        builder.registerTypeAdapter(SavedMazeComponent.class, new SavedMazeComponent.Serializer());
        builder.registerTypeAdapter(MazeRoom.class, new SavedMazeComponent.RoomSerializer());
        builder.registerTypeAdapter(SavedMazePath.class, new SavedMazePath.Serializer());

        return builder.create();
    }

    public static Gson getGson()
    {
        return gson;
    }

    @Nonnull
    @Override
    public String id()
    {
        return id;
    }

    @Override
    public void setID(@Nonnull String id)
    {
        this.id = id;
    }

    @Override
    public String displayString()
    {
        return StatCollector.translateToLocalFormatted("reccomplex.generationInfo.mazeComponent.title", mazeID);
    }

    @Override
    public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate)
    {
        return new TableDataSourceMazeGenerationInfo(navigator, delegate, this);
    }

    public static class Serializer implements JsonSerializer<MazeGenerationInfo>, JsonDeserializer<MazeGenerationInfo>
    {
        @Override
        public MazeGenerationInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(json, "MazeGenerationInfo");

            String id = JsonUtils.getJsonObjectStringFieldValueOrDefault(jsonObject, "id", "");

            String mazeID = JsonUtils.getJsonObjectStringFieldValue(jsonObject, "mazeID");
            SavedMazeComponent mazeComponent = gson.fromJson(jsonObject.get("component"), SavedMazeComponent.class);

            return new MazeGenerationInfo(id, mazeID, mazeComponent);
        }

        @Override
        public JsonElement serialize(MazeGenerationInfo src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", src.id);

            jsonObject.addProperty("mazeID", src.mazeID);
            jsonObject.add("component", gson.toJsonTree(src.mazeComponent));

            return jsonObject;
        }
    }
}
