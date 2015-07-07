/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com
Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

This file is part of ++Spicy - a Schema Mapping and Data Exchange Tool

++Spicy is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

++Spicy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ++Spicy.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibas.spicygui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Stroke;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.AnchorShapeFactory;
import org.openide.util.ImageUtilities;
import sun.awt.image.ToolkitImage;
import sun.swing.SwingUtilities2;

public class Costanti {

    //********************* MESSAGGI BUNDLE ***************************
    //AZIONI
    public static final String ACTION_NEW = "ACTION_NEW";
    public static final String ACTION_OPEN = "ACTION_OPEN";
    public static final String ACTION_LOAD_TGDS = "ACTION_LOAD_TGDS";
    public static final String ACTION_ADD_TARGET_INSTANCE = "ACTION_ADD_TARGET_INSTANCE";
    public static final String ACTION_ADD_SOURCE_INSTANCE = "ACTION_ADD_SOURCE_INSTANCE";
    public static final String ACTION_SAVE = "ACTION_SAVE";
    public static final String ACTION_SAVE_AS = "ACTION_SAVE_AS";
    public static final String ACTION_CLOSE = "ACTION_CLOSE";
    public static final String ACTION_PROPERTIES = "ACTION_PROPERTIES";
    public static final String ACTION_EXIT = "ACTION_EXIT";
    public static final String ACTION_NEW_TOOLTIP = "ACTION_NEW_TOOLTIP";
    public static final String ACTION_OPEN_TOOLTIP = "ACTION_OPEN_TOOLTIP";
    public static final String ACTION_LOAD_TGDS_TOOLTIP = "ACTION_LOAD_TGDS_TOOLTIP";
    public static final String ACTION_SAVE_TOOLTIP = "ACTION_SAVE_TOOLTIP";
    public static final String ACTION_SAVE_AS_TOOLTIP = "ACTION_SAVE_AS_TOOLTIP";
    public static final String ACTION_CLOSE_TOOLTIP = "ACTION_CLOSE_TOOLTIP";
    public static final String ACTION_PROPERTIES_TOOLTIP = "ACTION_PROPERTIES_TOOLTIP";
    public static final String ACTION_PROJECT = "ACTION_PROJECT";
    public static final String ACTION_PROJECT_TOOLTIP = "ACTION_PROJECT_TOOLTIP";
    public static final String ACTION_EXIT_TOOLTIP = "ACTION_EXIT_TOOLTIP";
    public static final String ACTION_VIEW_INSTANCES = "ACTION_VIEW_INSTANCES";
    public static final String ACTION_VIEW_INSTANCES_TOOLTIP = "ACTION_VIEW_INSTANCES_TOOLTIP";
    public static final String ACTION_SOLVING_MAPPING = "ACTION_SOLVING_MAPPING";
    public static final String ACTION_SOLVING_MAPPING_TOOLTIP = "ACTION_SOLVING_MAPPING_TOOLTIP";
    public static final String ACTION_TRANSLATE = "ACTION_TRANSLATE";
    public static final String ACTION_TRANSLATE_TOOLTIP = "ACTION_TRANSLATE_TOOLTIP";
    public static final String ACTION_SOLVING_AND_TRANSLATE = "ACTION_SOLVING_AND_TRANSLATE";
    public static final String ACTION_SOLVING_AND_TRANSLATE_TOOLTIP = "ACTION_SOLVING_AND_TRANSLATE_TOOLTIP";
    public static final String ACTION_INCLUSION_EXCLUSION = "ACTION_INCLUSION_EXCLUSION";
    public static final String ACTION_VIEW_FUNCTOR = "ACTION_VIEW_FUNCTOR";
    public static final String ACTION_VIEW_ALL_FUNCTORS = "ACTION_VIEW_ALL_FUNCTORS";
    public static final String ACTION_VIEW_PROVENANCE = "ACTION_VIEW_PROVENANCE";
    public static final String ACTION_VIEW_ALL_PROVENANCES = "ACTION_VIEW_ALL_PROVENANCES";
    public static final String ACTION_VIEW_ALL_VIRTUAL_NODE = "ACTION_VIEW_ALL_VIRTUAL_NODE";
    public static final String ACTION_VIEW_OIDs = "ACTION_VIEW_OIDs";
    public static final String ACTION_INCREASE_FONT = "ACTION_INCREASE_FONT";
    public static final String ACTION_DECREASE_FONT = "ACTION_DECREASE_FONT";
    public static final String ACTION_EXECUTE_SQL = "ACTION_EXECUTE_SQL";
    public static final String ACTION_EXECUTE_XQUERY = "ACTION_EXECUTE_XQUERY";
    public static final String ACTION_SHOW_HIDE_CONSTRAINTS = "ACTION_SHOW_HIDE_CONSTRAINTS";
    public static final String ACTION_SHOW_HIDE_JOIN_CONDITIONS = "ACTION_SHOW_HIDE_JOIN_CONDITIONS";
    public static final String ACTION_SHOW_HIDE_FUNCTIONAL_DEPENDENCIES = "ACTION_SHOW_HIDE_FUNCTIONAL_DEPENDENCIES";
    public static final String ACTION_SHOW_SCHEMA = "ACTION_SHOW_SCHEMA";
    public static final String ACTION_VIEW_TRANSFORMATIONS = "ACTION_VIEW_TRANSFORMATIONS";
    public static final String ACTION_SPICY = "ACTION_SPICY";
    public static final String ACTION_RUN_MATCHER = "ACTION_RUN_MATCHER";
    public static final String ACTION_LOAD_COMA_CORRESPONDENCES = "ACTION_LOAD_COMA_CORRESPONDENCES";
    public static final String ACTION_FIND_MAPPINGS = "ACTION_FIND_MAPPINGS";
    public static final String ACTION_FIND_MAPPINGS_ON_CORRESPONDENCES = "ACTION_FIND_MAPPINGS_ON_CORRESPONDENCES";
    public static final String ACTION_VIEW_BEST_MAPPINGS = "ACTION_VIEW_BEST_MAPPINGS";
    public static final String ACTION_VIEW_RANKED_TRANSFORMATIONS = "ACTION_VIEW_RANKED_TRANSFORMATIONS";
    public static final String ACTION_SLIDER = "ACTION_SLIDER";
    public static final String ACTION_GENERATE_XQUERY = "ACTION_GENERATE_XQUERY";
    public static final String ACTION_GENERATE_XQUERY_TOOLTIP = "ACTION_GENERATE_XQUERY_TOOLTIP";
    public static final String ACTION_GENERATE_SQL = "ACTION_GENERATE_SQL";
    public static final String ACTION_GENERATE_SQL_TOOLTIP = "ACTION_GENERATE_SQL_TOOLTIP";
    public static final String ACTION_VIEW_TGD_LIST = "ACTION_VIEW_TGD_LIST";
    public static final String ACTION_VIEW_TGD_LIST_TOOLTIP = "ACTION_VIEW_TGD_LIST_TOOLTIP";
    public static final String ACTION_VIEW_TGD = "ACTION_VIEW_TGD";
    public static final String ACTION_VIEW_TGD_TOOLTIP = "ACTION_VIEW_TGD_TOOLTIP";
    public static final String ACTION_EXPORT_TRANSLATED_INSTANCES = "ACTION_EXPORT_TRANSLATED_INSTANCES";
    public static final String ACTION_EXPORT_QUERY = "ACTION_EXPORT_QUERY";
    public static final String ACTION_RANK_TRANSFORMATIONS = "ACTION_RANK_TRANSFORMATIONS";
    public static final String ACTION_DUPLICATE_SET_NODE = "ACTION_DUPLICATE_SET_NODE";
    public static final String ACTION_DELETE_DUPLICATE_SET_CLONE_NODE = "ACTION_DELETE_DUPLICATE_SET_CLONE_NODE";
    public static final String ACTION_EDIT_SELECTION_CONDITION = "ACTION_EDIT_SELECTION_CONDITION";
    public static final String ACTION_SELECT_MAPPING_TASK = "ACTION_SELECT_MAPPING_TASK";
    public static final String ACTION_OPEN_GENERIC_TC = "ACTION_OPEN_GENERIC_TC";
    public static final String ACTION_REMOVE_SCENARIO = "ACTION_REMOVE_SCENARIO";
    public static final String ACTION_SETTING_ENGINE_CONFIGURATION = "ACTION_SETTING_ENGINE_CONFIGURATION";
    public static final String ACTION_VIEW_COMPOSITION = "ACTION_VIEW_COMPOSITION";
    public static final String ACTION_VIEW_COMPOSITION_TOOLTIP = "ACTION_VIEW_COMPOSITION_TOOLTIP";
    public static final String ACTION_ADD_MAPPING_IN_COMPOSITION_SCENE = "ACTION_ADD_MAPPING_IN_COMPOSITION_SCENE";
    public static final String ACTION_CREATE_COMPOSITION_SCENE = "ACTION_CREATE_COMPOSITION_SCENE";
    //VARIE BOTTONI
    public static final String CANCEL_BUTTON = "Cancel_button";
    public static final String CLOSE_BUTTON = "Close_button";
    public static final String TRAY_BUTTON = "Tray_button";
    //MESSAGGI
    public static final String SPICY_NAME = "++Spicy";
    public static final String ECCEZIONE_SCHEMI = "EccezioneSchemi";
    public static final String CARICAMENTO_SCHEMI_OK = "CaricamentoSchemiOk";
    public static final String CAMPO_NULLO = "CampoNullo";
    public static final String ERROR_DATE_TYPE = "Error_date_type";
    public static final String GENERIC_ERROR = "Generic_error";
    public static final String GENERIC_DELETE = "Generic_delete";
    public static final String GENERIC_WARNING = "Generic_Warning";
    public static final String SYNTAX_WARNING = "Syntax_Warning";
    public static final String CORRUPTED_FILE = "CORRUPTED_FILE";
    public static final String INCLUDE_NODES_WARNING = "INCLUDE_NODES_WARNING";
    public static final String NOT_MODIFIED = "NOT_MODIFIED";
    public static final String NEW_ERROR = "NEW_ERROR";
    public static final String OPEN_ERROR = "OPEN_ERROR";
    public static final String SAVE_ERROR = "SAVE_ERROR";
    public static final String EXPORT_ERROR = "EXPORT_ERROR";
    public static final String SAVE_OK = "SAVE_OK";
    public static final String SAVE_ON_CLOSE = "SAVE_ON_CLOSE";
    public static final String CREATE_AUTOMATIC_JOINCONDITION_SOURCE = "CREATE_AUTOMATIC_JOINCONDITION_SOURCE";
    public static final String CREATE_AUTOMATIC_JOINCONDITION_TARGET = "CREATE_AUTOMATIC_JOINCONDITION_TARGET";
    public static final String EXPORT_OK = "EXPORT_OK";
    public static final String ADD_INSTANCE_OK = "ADD_INSTANCE_OK";
    public static final String EMPTY_CORRESPONDENCES = "EMPTY_CORRESPONDENCES";
    public static final String DISCARD_CANDIDATE_CORRESPONDENCES = "DISCARD_CANDIDATE_CORRESPONDENCES";
    public static final String REALLY_CLOSE = "REALLY_CLOSE";
    public static final String CHECK_FOR_MINIMIZE = "CHECK_FOR_MINIMIZE";
    public static final String FILE_EXISTS = "FILE_EXISTS";
    public static final String NOT_MAPPED = "NOT_MAPPED";
    public static final String SINGLE_TRANSFORMATION = "SINGLE_TRANSFORMATION";
    public static final String NOT_LEGAL = "NOT_LEGAL";
    public static final String DELETE_EXCLUSIONE_CORRESPONDENCES = "DELETE_EXCLUSIONE_CORRESPONDENCES";
    public static final String TRANSFORMATION = "TRANSFORMATION";
    public static final String LOAD = "LOAD";
    public static final String EXPORT = "EXPORT";
    public static final String WARNING_NOT_TARGET_INSTANCES = "WARNING_NOT_TARGET_INSTANCES";
    public static final String FIND_VALUE_CORRESPONDENCES = "WARNING_FIND_VALUE_CORRESPONDENCES";
    public static final String TOGGLE_MANDATORY = "TOGGLE_MANDATORY";
    public static final String TOGGLE_FOREIGN = "TOGGLE_FOREIGN";
    public static final String DUPLICATION_NO = "DUPLICATION_NO";
    public static final String DELETE_DUPLICATION_NO = "DELETE_DUPLICATION_NO";
    public static final String MESSAGE_NO_RELATIONAL_DATASOURCE = "MESSAGE_NO_RELATIONAL_DATASOURCE";
    public static final String MESSAGE_QUERY_EXECUTED = "MESSAGE_QUERY_EXECUTED";
    public static final String MESSAGE_QUERY_NOT_EXECUTED = "MESSAGE_QUERY_NOT_EXECUTED";
    public static final String NOT_SUPPORTED_EXTENSTION = "NOT_SUPPORTED_EXTENSTION";
    public static final String REFRESH_TGD = "REFRESH_TGD";
    public static final String REFRESH_SQL = "REFRESH_SQL";
    public static final String REFRESH_XQUERY = "REFRESH_XQUERY";
    public static final String INFORMATION_ON_TRAY_START = "INFORMATION_ON_TRAY_START";
    public static final String INFORMATION_ON_TRAY_END_P = "INFORMATION_ON_TRAY_END_P";
    public static final String INFORMATION_ON_TRAY_END_S = "INFORMATION_ON_TRAY_END_S";
    public static final String JFILECHOOSER_FOLDER_FILE_NAME = "JFILECHOOSER_FOLDER_FILE_NAME";
    public static final String JFILECHOOSER_FOLDER_TYPE_FILE = "JFILECHOOSER_FOLDER_TYPE_FILE";
    public static final String NO_INSTANCES_FOR_COMPOSITION = "NO_INSTANCES_FOR_COMPOSITION";
    //MESSAGGI COMPOSIZIONE
    public static final String NOT_ADDED_IN_COMPOSITION = "NOT_ADDED_IN_COMPOSITION";
    public static final String ALREADY_CREATED_COMPOSITION = "ALREADY_CREATED_COMPOSITION";
    public static final String LOAD_DATASOURCE_FOR_CHAIN = "LOAD_DATASOURCE_FOR_CHAIN";
    public static final String DELETE_WIDGET_COMPOSITION = "DELETE_WIDGET_COMPOSITION";
    //MESSAGGI JOIN CONDITION
    public static final String JOIN_DIALOG_TITLE = "JOIN_DIALOG_TITLE";
    public static final String JOIN_DIALOG_MESSAGE = "JOIN_DIALOG_MESSAGE";
    public static final String JOIN_DIALOG_SINGLE = "JOIN_DIALOG_SINGLE";
    public static final String JOIN_DIALOG_MULTIPLE = "JOIN_DIALOG_MULTIPLE";
    public static final String JOIN_DIALOG_CANCEL = "JOIN_DIALOG_CANCEL";
    public static final String JOINCONDITION_DUPLICATE = "JOINCONDITION_DUPLICATE";
    public static final String JOINCONDITION_NO = "JOINCONDITION_NO";
    //MESSAGGI MULTIPL JOIN CONDITION
    public static final String MULTIPLE_JOIN_DIALOG_TITLE = "MULTIPLE_JOIN_DIALOG_TITLE";
    //INFO CONNESSIONI
    public static final String CONNECTION_CONSTRAINT = "connectionConstraint";
    public static final String CONNECTION_CONSTRAINT_TGD = "connectionConstraintTGD";
    public static final String CONNECTION_CONSTRAINT_SPICY = "connectionConstraintSpicy";
    public static final String JOIN_CONNECTION_CONSTRAINT = "joinConnectionConstraint";
    public static final String JOIN_CONNECTION_CONSTRAINT_TGD = "joinConnectionConstraintTGD";
    public static final String JOIN_CONNECTION_CONSTRAINT_SPICY = "joinConnectionConstraintSpicy";
    public static final String JOIN_CONNECTION_CONSTRAINT_SOURCE = "joinConnectionConstraintSource";
    public static final String JOIN_CONNECTION_CONSTRAINT_TARGET = "joinConnectionConstraintTarget";
    public static final String JOIN_CONNECTION_CONSTRAINT_SOURCE_SPICY = "joinConnectionConstraintSourceSpicy";
    public static final String JOIN_CONNECTION_CONSTRAINT_TARGET_SPICY = "joinConnectionConstraintTargetSpicy";
    public static final String SHOW_HIDE_INFO_CONNECTION = "Show_Hide_info_connection";
    public static final String HIDE_INFO_CONNECTION = "Hide_info_connection";
    public static final String DELETE_CONNECTION = "Delete_connection";
    public static final String PROPRIETA_CONNESSIONI = "Connection_properties";
    public static final String IMPLIED = "Implied";
    //INFO CONNESSIONI -- EDIT CONFIDENCE
    public static final String INPUT_TEXT_CONFIDENCE_TITLE = "Input_text_confidence_title";
    public static final String INPUT_TEXT_CONFIDENCE_LABEL = "Input_text_confidence_label";
    public static final String ERROR_LABEL_CONFIDENCE = "Error_label_confidence";
    public static final String TOOL_TIP_CONFIDENCE = "Tool_tip_confidence";
    //EDIT SELECTION CONDITION
    public static final String INPUT_TEXT_SELECTION_CONDITION_TITLE = "INPUT_TEXT_SELECTION_CONDITION_TITLE";
    //ZONA INTERMEDIA
    public static final String CREATE_FUNCTION = "Create_function";
    public static final String CREATE_ATTRIBUTE_GROUP = "Create_Attribute_Group";
    public static final String CREATE_FUNCTIONAL_DEPENDECY = "Create_Functional_Dependency";
    public static final String DELETE_FUNCTIONAL_DEPENDECY = "Delete_Functional_Dependency";
    public static final String CREATE_CONSTANT = "Create_constant";
    public static final String DELETE_INTERMEDIE = "Delete_intermedie";
    //ZONA INTERMEDIA -- DIALOG
    //ZONA COMPOSIZIONE
    public static final String CREATE_MERGE_WIDGET = "Create_Merge_Widget";
    public static final String CREATE_UNDEFINED_CHAIN_WIDGET = "Create_undefined_chain_widget";
    //ZONA COMPOSIZIONE -- DIALOG
    public static final String INPUT_TEXT_CONSTANT_TITLE = "Input_text_constant_title";
    public static final String INPUT_TEXT_CONSTANT_LABEL = "Input_text_constant_label";
    public static final String INPUT_TEXT_CONSTANT_RADIOS = "Input_text_constant_radio_string";
    public static final String INPUT_TEXT_CONSTANT_RADION = "Input_text_constant_radio_number";
    public static final String INPUT_TEXT_CONSTANT_RADIOF = "Input_text_constant_radio_function";
    public static final String INPUT_TEXT_CONSTANT_FUNCTION1 = "Input_text_constant_function_date";
    public static final String INPUT_TEXT_CONSTANT_FUNCTION2 = "Input_text_constant_function_increment";
    //ZONA GLASS-PANE
    public static final String DELETE_ALL_CONNECTIONS = "DELETE_ALL_CONNECTIONS";
    public static final String SET_MULTIPLE_JOIN_SESSION = "SET_MULTIPLE_JOIN_SESSION";
    //ZONA WIZARD
    public static final String EtichettaSchemaSource = "EtichettaSchemaSource";
    public static final String EtichettaIstanzeSource = "EtichettaIstanzeSource";
    public static final String LABEL_DATA_SOURCE_TYPE = "LABEL_DATA_SOURCE_TYPE";
    public static final String LABEL_DRIVER = "LABEL_DRIVER";
    public static final String LABEL_URI = "LABEL_URI";
    public static final String LABEL_USER_NAME = "LABEL_USER_NAME";
    public static final String LABEL_PASSWORD = "LABEL_PASSWORD";
    public static final String TEXT_URI = "TEXT_URI";
    public static final String TEXT_PASSWORD = "TEXT_PASSWORD";
    public static final String TEXT_USER = "TEXT_USER";
    //ZONA SQLDIALOG
    public static final String SQL_DIALOG = "SQL_DIALOG";
    public static final String LABEL_TMP_DATABASE_NAME = "LABEL_TMP_DATABASE_NAME";
    public static final String LABEL_CHECK_RECREATE = "LABEL_CHECK_RECREATE";
    public static final String LABEL_SCHEMA_SOURCE = "LABEL_SCHEMA_SOURCE";
    public static final String LABEL_INSTANCE_SOURCE = "LABEL_INSTANCE_SOURCE";
    public static final String LABEL_SCHEMA_TARGET = "LABEL_SCHEMA_TARGET";
    //EXECUTE SQL
    public static final String INFORMATION_NEEDED = "INFORMATION_NEEDED";
    public static final String LABEL_DRIVER_SUPPORT = "LABEL_DRIVER_SUPPORT";
    public static final String LABEL_URI_SUPPORT = "LABEL_URI_SUPPORT";
    public static final String LABEL_USER_NAME_SUPPORT = "LABEL_USER_NAME_SUPPORT";
    public static final String LABEL_PASSWORD_SUPPORT = "LABEL_PASSWORD_SUPPORT";
    //DIALOG ENGINE CONFIGURATION
    public static final String COMBO_AUTO_SORT = "COMBO_AUTO_SORT";
    public static final String COMBO_SORT = "COMBO_SORT";
    public static final String COMBO_NO_SORT = "COMBO_NO_SORT";
    public static final String COMBO_AUTO_SKOLEM_TABLE = "COMBO_AUTO_SKOLEM_TABLE";
    public static final String COMBO_SKOLEM_TABLE = "COMBO_SKOLEM_TABLE";
    public static final String COMBO_NO_SKOLEM_TABLE = "COMBO_NO_SKOLEM_TABLE";
    //END ENGINE CONFIGURATION
    //VISTE
    public static final String CARICAMENTO_WIZARD_PANEL = "caricamento wizard panel";
    public static final String CARIMENTO_VISUAL_PANEL = "caricamento visual panel";
    public static final String SCHEMI_ALBERI_TOP_COMPONENT = "SCHEMI_ALBERI_TOP_COMPONENT";
    public static final String SCHEMI_ALBERI_TOP_COMPONENT_TOOLTIP = "SCHEMI_ALBERI_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_SOURCE_INSTANCES_TOP_COMPONENT = "VIEW_SOURCE_INSTANCES_TOP_COMPONENT";
    public static final String VIEW_SOURCE_INSTANCES_TOP_COMPONENT_TOOLTIP = "VIEW_SOURCE_INSTANCES_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_TRANSFORMATIONS_TOP_COMPONENT = "VIEW_TRANSFORMATIONS_TOP_COMPONENT";
    public static final String VIEW_TRANSFORMATIONS_TOP_COMPONENT_TOOLTIP = "VIEW_TRANSFORMATIONS_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_SPICY_TOP_COMPONENT = "VIEW_SPICY_TOP_COMPONENT";
    public static final String VIEW_SPICY_TOP_COMPONENT_TOOLTIP = "VIEW_SPICY_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_BEST_MAPPINGS_TOP_COMPONENT = "VIEW_BEST_MAPPINGS_TOP_COMPONENT";
    public static final String VIEW_BEST_MAPPINGS_TOP_COMPONENT_TOOLTIP = "VIEW_BEST_MAPPINGS_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_RANKED_TRANSFORMATIONS_TOP_COMPONENT = "VIEW_RANKED_TRANSFORMATIONS_TOP_COMPONENT";
    public static final String VIEW_RANKED_TRANSFORMATIONS_TOP_COMPONENT_TOOLTIP = "VIEW_RANKED_TRANSFORMATIONS_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_SELECTED_TRANSFORMATION_TOP_COMPONENT = "VIEW_SELECTED_TRANSFORMATION_TOP_COMPONENT";
    public static final String VIEW_SELECTED_TRANSFORMATION_TOP_COMPONENT_TOOLTIP = "VIEW_SELECTED_TRANSFORMATION_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_GENERATE_XQUERY_TOP_COMPONENT = "VIEW_GENERATE_XQUERY_TOP_COMPONENT";
    public static final String VIEW_GENERATE_XQUERY_TOP_COMPONENT_TOOLTIP = "VIEW_GENERATE_XQUERY_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_GENERATE_SQL_TOP_COMPONENT = "VIEW_GENERATE_SQL_TOP_COMPONENT";
    public static final String VIEW_GENERATE_SQL_TOP_COMPONENT_TOOLTIP = "VIEW_GENERATE_SQL_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_PROJECT_TREE_TOP_COMPONENT = "VIEW_PROJECT_TREE_TOP_COMPONENT";
    public static final String VIEW_PROJECT_TREE_TOP_COMPONENT_TOOLTIP = "VIEW_PROJECT_TREE_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_TGD_LIST_TOP_COMPONENT = "VIEW_TGD_LIST_TOP_COMPONENT";
    public static final String VIEW_TGD_LIST_TOP_COMPONENT_TOOLTIP = "VIEW_TGD_LIST_TOP_COMPONENT_TOOLTIP";
    public static final String VIEW_COMPOSITION_TOP_COMPONENT = "VIEW_COMPOSITION_TOP_COMPONENT";
    public static final String VIEW_COMPOSITION_TOP_COMPONENT_TOOLTIP = "VIEW_COMPOSITION_TOP_COMPONENT_TOOLTIP";
    //COLORS
    public static final Color COLOR_CONNECTION_CONSTRAINT_DEFAULT_CORRESPONDENCE = Color.BLACK;
    public static final Color COLOR_CONNECTION_CONSTRAINT_DEFAULT = Color.GRAY;
    public static final Color COLOR_CONNECTION_CONSTRAINT_SELECTED = Color.RED;

    public static Color getIntermediateColor() {
        float[] colori = new float[3];
        Color.RGBtoHSB(254, 255, 213, colori);
        return Color.getHSBColor(colori[0], colori[1], colori[2]);
    }
    public static final Stroke BASIC_STROKE = new BasicStroke(0.5f);
    public static final Stroke DASHED_STROKE = new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{3}, 0);
    public static final Stroke DASHED_STROKE_THICK = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{3}, 0);
    public static final AnchorShape ANCHOR_SHAPE = AnchorShapeFactory.createTriangleAnchorShape(5, false, false);
    public static final Stroke CONSTRAINTS_STROKE = new BasicStroke(0.4f);    //OFFSET
    public static final int OFF_SET_X_WIDGET_BARRA = 13;
    public static final int OFF_SET_Y_WIDGET_BARRA = 12;
    public static final int OFFSET_X_WIDGET_SOURCE = 40;
    public static final int OFFSET_Y_WIDGET_SOURCE = 4;
    public static final int INDEX_MAIN_LAYER = 0;
    public static final int INDEX_CONNECTION_LAYER = 1;
    public static final int INDEX_CONSTRAINTS_LAYER = 2;
    //BEAN
    public static final String LAST_ACTION_BEAN = "last action bean";
    public static final String ACTUAL_SAVE_FILE = "actual_save_file";
    public static final String CONNECTION_CONSTRAINT_SOURCE = "connectionConstraintSource";
    public static final String CONNECTION_CONSTRAINT_TARGET = "connectionConstraintTarget";
    public static final String CONNECTION_CONSTRAINT_SOURCE_SPICY = "connectionConstraintSourceSpicy";
    public static final String CONNECTION_CONSTRAINT_TARGET_SPICY = "connectionConstraintTargetSpicy";
    public static final String XML_CONFIGURATION_SOURCE = "XML_CONFIGURATION_SOURCE";
    public static final String XML_CONFIGURATION_TARGET = "XML_CONFIGURATION_TARGET";
    public static final String RELATIONAL_CONFIGURATION_SOURCE = "RELATIONAL_CONFIGURATION_SOURCE";
    public static final String RELATIONAL_CONFIGURATION_TARGET = "RELATIONAL_CONFIGURATION_TARGET";
    public static final String NEW_MAPPING_TASK_PM = "NEW_MAPPING_TASK_PM";
    public static final String MAPPINGTASK_SHOWED = "MAPPINGTASK_SHOWED";
//    public static final String MAPPINGTASK = "mappingTask";
    public static final String BEST_MAPPING_TASKS = "bestMappingTasks";
    public static final String RANKED_TRANSFORMATIONS = "rankedTransformations";
    public static final String MAPPINGTASK_SELECTED = "selected mappingTask";
    public static final String SCENARIOS = "SCENARIOS";
    public static final String CURRENT_SCENARIO = "CURRENT_SCENARIO";
    public static final String CONNECTION_SELECTED = "connectionWidgetSelected";
    public static final String CHECK_FIND_BEST_MAPPING = "check find best mapping";
    public static final String LINE_COORDINATES_COLLECTIONS = "LineCoordinatesCollections";
    public static final String CONSTRAINTS_WIDGET_COLLECTIONS = "ConstraintsWidgetCollections";
    public static final String CREATING_JOIN_SESSION = "CREATING_JOIN_SESSION";
    public static final String JOIN_SESSION_SOURCE = "JOIN_SESSION_SOURCE";
    public static final String JOIN_SESSION_TARGET = "JOIN_SESSION_TARGET";
    public static final String JOIN_CONSTRIANTS = "JOIN_CONSTRIANTS";
    public static final String JOIN_CONDITION = "JOIN_CONDITION";
    public static final String FROM_PATH_NODES = "FROM_PATH_NODES";
    public static final String RECREATE_TREE = "RECREATE_TREE";
    public static final String SELECTION_CONDITON_INFO = "SELECTION_CONDITON_INFO";
    public static final String TGD_SESSION = "TGD_SESSION";
    //ICONE
    public static final String ICONA_SCHEMA_ALBERI = "misc/icons/table_relationship.png";
    public static final String ICONA_TRANSLATE = "misc/icons/table_gear.png";
    public static final String ICONA_GENERATE_TRANSFORMATIONS = "misc/icons/chart_line_edit.png";
    public static final String ICONA_GENERATE_AND_TRANSLATE = "misc/icons/chart_line_link.png";
    public static final String ICONA_PIN_SOURCE = "misc/icons/immagineTrasparente10x80.png";
    public static final String ICONA_PIN_TARGET = "misc/icons/immagineTrasparente10x20.png";
    public static final String ICONA_PIN_KEY = "misc/icons/immagineTrasparente5x5.png";
    public static final String ICONA_CONSTANT = "misc/icons/constant.png";
    public static final String ICONA_FUNCTION = "misc/icons/function.png";
    public static final String ICONA_MERGE_WIDGET = "misc/icons/merge_arrow.png";
    public static final String ICONA_ATTRIBUTE_GROUP = "misc/icons/attribute_group.png";
//    public static final String ICONA_FUNCTIONAL_DEPENDENCY = "misc/icons/functional_dependency.png";
    public static final String ICONA_FUNCTIONAL_DEPENDENCY = "misc/icons/func_dep_ok.png";
    public static final String ICONA_INFORMATION = "misc/icons/information.png";
    public static final String ICONA_COMMENT = "misc/icons/comment_edit.png";
    public static final String ICONA_COMMENT_TRASPARENT = "misc/icons/comment_trasparent.png";
    public static final String ICONA_PUNTO = "misc/icons/bullet_black.png";
    public static final String ICONA_NEW = "misc/icons/chart_line_add.png";
    public static final String ICONA_OPEN = "misc/icons/folder_page_white.png";
    public static final String ICONA_ADD_TARGET_INSTANCE = "misc/icons/page_load_instance.png";
    public static final String ICONA_CLOSE = "misc/icons/close.png";
    public static final String ICONA_EXIT = "misc/icons/door_in.png";
    public static final String ICONA_SAVE = "misc/icons/disk.png";
    public static final String ICONA_VIEW = "misc/icons/view.png";
    public static final String ICONA_MOVE = "misc/icons/moveIcon.png";
    public static final String ICONA_CONFIDENCE = "misc/icons/page_white_c.png";
//    public static final String ICONA_FILTRO = "misc/icons/filter.png";
    public static final String ICONA_VIEW_TRASFORMATION = "misc/icons/book_open.png";
    public static final String ICONA_SHOW_HIDE_CONSTRAINTS = "misc/icons/layers_constraints.png";
    public static final String ICONA_SHOW_HIDE_JOIN_CONDITIONS = "misc/icons/layers_join_conditions.png";
    public static final String ICONA_SHOW_HIDE_FUNCTIONAL_DEPENDENCIES = "misc/icons/layers_functional_dependencies.png";
    public static final String ICONA_SPICY = "misc/icons/spicy.gif";
    public static final String ICONA_VIEW_BEST_MAPPINGS = "misc/icons/bestMappings.png";
    public static final String ICONA_VIEW_RANKED_TRANSFORMATIONS = "misc/icons/bestMappings.png";
    public static final String ICONA_GENERATE_QUERY = "misc/icons/generate_query.png";
    public static final String ICONA_PROJECT = "misc/icons/project.png";
    public static final String ICONA_VIEW_TGD = "misc/icons/table_tgd.png";
    public static final String ICONA_VIEW_COMPOSITION = "misc/icons/composition.png";
    //ICONE NUMERI
    public static final String ICONA_NUMBER_1 = "misc/icons/numbers/number1.png";
    public static final String ICONA_NUMBER_2 = "misc/icons/numbers/number2.png";
    public static final String ICONA_NUMBER_3 = "misc/icons/numbers/number3.png";
    public static final String ICONA_NUMBER_4 = "misc/icons/numbers/number4.png";
    public static final String ICONA_NUMBER_5 = "misc/icons/numbers/number5.png";
    public static final String ICONA_NUMBER_6 = "misc/icons/numbers/number6.png";
    public static final String ICONA_NUMBER_7 = "misc/icons/numbers/number7.png";
    public static final String ICONA_NUMBER_8 = "misc/icons/numbers/number8.png";
    public static final String ICONA_NUMBER_9 = "misc/icons/numbers/number9.png";
    public static final String ICONA_NUMBER_0 = "misc/icons/numbers/number0.png";
    //ICONE ALTRO
    public static final String UNDEFINED_IMAGE = "misc/icons/undefined.png";
    public static final Image IMAGE_DND_ACCEPT = ImageUtilities.loadImage("misc/icons/dnd/accept.png");
    public static final Image IMAGE_DND_DENIED = ImageUtilities.loadImage("misc/icons/dnd/denied.png");
    // ICONE ALBERI
    public static final ImageIcon ICONA_XML_ROOT_ALBERO = new ImageIcon(ImageUtilities.loadImage("misc/icons/xml.png"));
    public static final ImageIcon ICONA_RELATIONAL_ROOT_ALBERO = new ImageIcon(ImageUtilities.loadImage("misc/icons/relational.png"));
    public static final ImageIcon ICONA_ELEMENTO_ALBERO = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_database.png"));
    public static final ImageIcon ICONA_ELEMENTO_SET = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_stack.png"));
    public static final ImageIcon ICONA_RELATIONAL_ELEMENTO_SET = new ImageIcon(ImageUtilities.loadImage("misc/icons/relational_set.png"));
    public static final ImageIcon ICONA_ELEMENTO_TUPLE = new ImageIcon(ImageUtilities.loadImage("misc/icons/tupleNode.png"));
    public static final ImageIcon ICONA_RELATIONAL_ELEMENTO_TUPLE = new ImageIcon(ImageUtilities.loadImage("misc/icons/relational_tuple.png"));
    public static final ImageIcon ICONA_ELEMENTO_KEY_ALBERO = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_key.png"));
    public static final ImageIcon ICONA_ELEMENTO_FOREIGN_KEY_ALBERO = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_link.png"));
    public static final ImageIcon ICONA_ELEMENTO_KEY_FOREIGN_KEY_ALBERO = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_lightning.png"));
    public static final ImageIcon ICONA_EXCLUSION_NODE = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_delete.png"));
    public static final ImageIcon ICONA_INSTANCE_ROOT = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_code_red.png"));
    public static final ImageIcon ICONA_INSTANCE_INTERMEDIATE = new ImageIcon(ImageUtilities.loadImage("misc/icons/page_white_code.png"));
    public static final ImageIcon ICONA_INSTANCE_LEAF = new ImageIcon(ImageUtilities.loadImage("misc/icons/bullet.png"));
    public static final ImageIcon ICONA_OBJECT_ROOT_ALBERO = new ImageIcon(ImageUtilities.loadImage("misc/icons/duke.png"));
    public static final ImageIcon ICONA_OBJECT_HIERARCH = new ImageIcon(ImageUtilities.loadImage("misc/icons/hierarch.png"));
    public static final ImageIcon ICONA_OBJECT_ASSOCIATION = new ImageIcon(ImageUtilities.loadImage("misc/icons/association.png"));
    public static final ImageIcon ICONA_OBJECT_TUPLE_HIERARCH = new ImageIcon(ImageUtilities.loadImage("misc/icons/java.gif"));
    public static final ImageIcon ICONA_OBJECT_TUPLE_ASSOCIATION = new ImageIcon(ImageUtilities.loadImage("misc/icons/link.png"));    //ALTRO
    public static final ImageIcon ICONA_SELECTION_CONDITION_SET = new ImageIcon(ImageUtilities.loadImage("misc/icons/selection_condition_set.png"));    //ALTRO
    public static final ImageIcon ICONA_PROJECT_TREE_TC = new ImageIcon(ImageUtilities.loadImage("misc/icons/tc_tree.png"));
    public static final ImageIcon ICONA_PROJECT_TREE_TC_SELECTED = new ImageIcon(ImageUtilities.loadImage("misc/icons/selected_tc_tree.png"));
    public static final ImageIcon ICONA_PROJECT_TREE_ROOT = new ImageIcon(ImageUtilities.loadImage("misc/icons/project.png"));
    public static final String ICONA_PROJECT_TREE_SCENARIO_SELECTED_STRING = "misc/icons/selected_scenario.png";
    public static final String ICONA_PROJECT_TREE_SCENARIO_STRING = "misc/icons/scenario.png";
    public static final ImageIcon ICONA_PROJECT_TREE_SCENARIO = new ImageIcon(ImageUtilities.loadImage(ICONA_PROJECT_TREE_SCENARIO_STRING));
    public static final ImageIcon ICONA_PROJECT_TREE_SCENARIO_SELECTED = new ImageIcon(ImageUtilities.loadImage(ICONA_PROJECT_TREE_SCENARIO_SELECTED_STRING));
    public static final String PIN_WIDGET_TREE = "Pin_Tree";
    public static final String PIN_WIDGET_TREE_TGD = "Pin_Tree_TGD";
    public static final String PIN_WIDGET_TREE_SPICY = "Pin_Tree_Spicy";
    public static final int MAX_NODI = 50;
    public static final String DATASOURCE_TYPE_RELATIONAL = "DATASOURCE_TYPE_RELATIONAL";
    public static final String DATASOURCE_TYPE_XML = "DATASOURCE_TYPE_XML";
    public static final String SOURCE = "Source";
    public static final String TARGET = "Target";
    public static final String SOLUTION = "SOLUTION";
    public static final String MESSAGE_BEST_MAPPINGS = "MESSAGE_BEST_MAPPINGS";
    public static final String MESSAGE_NO_BEST_MAPPINGS = "MESSAGE_NO_BEST_MAPPINGS";
    public static final String MESSAGE_RANKED_TRANSFORMATIONS = "MESSAGE_RANKED_TRANSFORMATIONS";    //TREE TYPE
    public static final String TREE_SOURCE = "source";
    public static final String TREE_TARGET = "target";
    public static final String KEY = "key_type";
    public static final String FOREIGN_KEY = "foreign_key_type";
    public static final String INTERMEDIE = "intermedie";
    public static final String COMPOSITION_TYPE = "composition type";
    public static final String INTERMEDIE_BARRA = "intermedie_barra";    //SPICY
    public static final String RANK = "RANK";
    public static final String QUALITY = "QUALITY";
    public static final String FLUSSO_SPICY = "Spicy";
    public static final String SELECTED_TRANSFORMATION = "SELECTED_TRANSFORMATION";    // ATTENZIONE: COSTANTE DUPLICATA... (Vedi SpicyModelConstants)
    public static final String TYPE_RELATIONAL = "relational";
    public static final String SET_ROOT_IN_COMPOSITION = "SET_ROOT_IN_COMPOSITION";
}
