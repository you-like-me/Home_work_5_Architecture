import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Scanner;

/**
 * Главный класс программы, реализующий пользовательский интерфейс и взаимодействие с редактором 3D графики.
 */
public class Program {

    static Scanner scanner = new Scanner(System.in);

    /**
     * Точка входа в программу.
     */
    public static void main(String[] args) {
        Editor3D editor3D = new Editor3D();
        boolean f = true;
        while (f) {
            System.out.println("*** МОЙ 3D РЕДАКТОР ***");
            System.out.println("=======================");
            System.out.println("1. Открыть проект");
            System.out.println("2. Сохранить проект");
            System.out.println("3. Отобразить параметры проекта");
            System.out.println("4. Отобразить все модели проекта");
            System.out.println("5. Отобразить все текстуры проекта");
            System.out.println("6. Выполнить рендер всех моделей");
            System.out.println("7. Выполнить рендер модели");
            System.out.println("8. Добавить 3D модель");
            System.out.println("9. Удалить 3D модель");
            System.out.println("10. Добавить текстуру");
            System.out.println("11. Удалить текстуру");
            System.out.println("0. ЗАВЕРШЕНИЕ РАБОТЫ ПРИЛОЖЕНИЯ");
            System.out.print("Пожалуйста, выберите пункт меню: ");
            if (scanner.hasNextInt()) {
                int no = scanner.nextInt();
                scanner.nextLine();
                try {
                    switch (no) {
                        case 0:
                            System.out.println("Завершение работы приложения");
                            f = false;
                            break;
                        case 1:
                            System.out.print("Укажите наименование файла проекта: ");
                            String fileName = scanner.nextLine();
                            editor3D.openProject(fileName);
                            System.out.println("Проект успешно открыт.");
                            break;
                        case 3:
                            editor3D.showProjectSettings();
                            break;
                        case 4:
                            editor3D.printAllModels();
                            break;
                        case 5:
                            editor3D.printAllTextures();
                            break;
                        case 6:
                            editor3D.renderAll();
                            break;
                        case 7:
                            System.out.print("Укажите номер модели: ");
                            if (scanner.hasNextInt()) {
                                int modelNo = scanner.nextInt();
                                scanner.nextLine();
                                editor3D.renderModel(modelNo);
                            } else {
                                System.out.println("Номер модели указан некорректно.");
                            }
                            break;
                        case 8:
                            System.out.print("Укажите имя новой модели: ");
                            String modelName = scanner.nextLine();
                            editor3D.addModel(new Model3D(modelName));
                            break;
                        case 9:
                            System.out.print("Укажите номер модели для удаления: ");
                            if (scanner.hasNextInt()) {
                                int modelNo = scanner.nextInt();
                                scanner.nextLine();
                                editor3D.removeModel(modelNo);
                            } else {
                                System.out.println("Номер модели указан некорректно.");
                            }
                            break;
                        case 10:
                            System.out.print("Укажите имя новой текстуры: ");
                            String textureName = scanner.nextLine();
                            editor3D.addTexture(new Texture(textureName));
                            break;
                        case 11:
                            System.out.print("Укажите номер текстуры для удаления: ");
                            if (scanner.hasNextInt()) {
                                int textureNo = scanner.nextInt();
                                scanner.nextLine();
                                editor3D.removeTexture(textureNo);
                            } else {
                                System.out.println("Номер текстуры указан некорректно.");
                            }
                            break;
                        default:
                            System.out.println("Укажите корректный пункт меню.");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Укажите корректный пункт меню.");
                scanner.nextLine();
            }
        }
    }
}

/**
 * Класс, представляющий редактор 3D графики.
 */
class Editor3D implements UILayer {

    private ProjectFile projectFile;
    private BusinessLogicalLayer businessLogicalLayer;

    private DatabaseAccess databaseAccess;
    private Database database;

    /**
     * Инициализация редактора.
     */
    private void initialize() {
        database = new EditorDatabase(projectFile);
        databaseAccess = new EditorDatabaseAccess(database);
        businessLogicalLayer = new EditorBusinessLogicalLayer(databaseAccess);
    }

    @Override
    public void openProject(String fileName) {
        this.projectFile = new ProjectFile(fileName);
        initialize();
    }

    @Override
    public void showProjectSettings() {
        // Предусловие
        checkProjectFile();

        System.out.println("*** Project v1 ***");
        System.out.println("******************");
        System.out.printf("fileName: %s\n", projectFile.getFileName());
        System.out.printf("setting1: %d\n", projectFile.getSetting1());
        System.out.printf("setting2: %s\n", projectFile.getSetting2());
        System.out.printf("setting3: %s\n", projectFile.getSetting3());
        System.out.println("******************");
    }

    @Override
    public void saveProject() {
        // Предусловие
        checkProjectFile();

        database.save();
        System.out.println("Изменения успешно сохранены.");
    }

    @Override
    public void printAllModels() {
        // Предусловие
        checkProjectFile();

        ArrayList<Model3D> models = (ArrayList<Model3D>) businessLogicalLayer.getAllModels();
        for (int i = 0; i < models.size(); i++) {
            System.out.printf("===%d===\n", i);
            System.out.println(models.get(i));
            for (Texture texture : models.get(i).getTextures()) {
                System.out.printf("\t%s\n", texture);
            }
        }
    }

    @Override
    public void printAllTextures() {
        // Предусловие
        checkProjectFile();

        ArrayList<Texture> textures = (ArrayList<Texture>) businessLogicalLayer.getAllTextures();
        for (int i = 0; i < textures.size(); i++) {
            System.out.printf("===%d===\n", i);
            System.out.println(textures.get(i));
        }
    }

    @Override
    public void renderAll() {
        // Предусловие
        checkProjectFile();

        System.out.println("Подождите ...");
        long startTime = System.currentTimeMillis();
        businessLogicalLayer.renderAllModels();
        long endTime = (System.currentTimeMillis() - startTime);
        System.out.printf("Операция выполнена за %d мс.\n", endTime);
    }

    @Override
    public void renderModel(int i) {
        // Предусловие
        checkProjectFile();

        ArrayList<Model3D> models = (ArrayList<Model3D>) businessLogicalLayer.getAllModels();
        if (i < 0 || i > models.size() - 1) {
            throw new RuntimeException("Номер модели указан некорректно.");
        }
        System.out.println("Подождите ...");
        long startTime = System.currentTimeMillis();
        businessLogicalLayer.renderModel(models.get(i));
        long endTime = (System.currentTimeMillis() - startTime);
        System.out.printf("Операция выполнена за %d мс.\n", endTime);
    }

    @Override
    public void addModel(Model3D model) {
        // Предусловие
        checkProjectFile();

        businessLogicalLayer.addModel(model);
        System.out.println("3D модель успешно добавлена.");
    }

    @Override
    public void removeModel(int i) {
        // Предусловие
        checkProjectFile();

        ArrayList<Model3D> models = (ArrayList<Model3D>) businessLogicalLayer.getAllModels();
        if (i < 0 || i > models.size() - 1) {
            throw new RuntimeException("Номер модели указан некорректно.");
        }
        businessLogicalLayer.removeModel(models.get(i));
        System.out.println("3D модель успешно удалена.");
    }

    @Override
    public void addTexture(Texture texture) {
        // Предусловие
        checkProjectFile();

        businessLogicalLayer.addTexture(texture);
        System.out.println("Текстура успешно добавлена.");
    }

    @Override
    public void removeTexture(int i) {
        // Предусловие
        checkProjectFile();

        ArrayList<Texture> textures = (ArrayList<Texture>) businessLogicalLayer.getAllTextures();
        if (i < 0 || i > textures.size() - 1) {
            throw new RuntimeException("Номер текстуры указан некорректно.");
        }
        businessLogicalLayer.removeTexture(textures.get(i));
        System.out.println("Текстура успешно удалена.");
    }

    private void checkProjectFile() {
        if (projectFile == null) {
            throw new RuntimeException("Файл проекта не определен.");
        }
    }
}

/**
 * Интерфейс UI (User Interface).
 */
interface UILayer {

    /**
     * Открывает проект с указанным именем файла.
     *
     * @param fileName Имя файла проекта.
     */
    void openProject(String fileName);

    /**
     * Отображает параметры проекта.
     */
    void showProjectSettings();

    /**
     * Сохраняет проект.
     */
    void saveProject();

    /**
     * Выводит все модели проекта.
     */
    void printAllModels();

    /**
     * Выводит все текстуры проекта.
     */
    void printAllTextures();

    /**
     * Выполняет рендер всех моделей проекта.
     */
    void renderAll();

    /**
     * Выполняет рендер указанной модели проекта.
     *
     * @param i Номер модели для рендеринга.
     */
    void renderModel(int i);

    /**
     * Добавляет 3D модель в проект.
     *
     * @param model Добавляемая модель.
     */
    void addModel(Model3D model);

    /**
     * Удаляет 3D модель из проекта.
     *
     * @param i Номер модели для удаления.
     */
    void removeModel(int i);

    /**
     * Добавляет текстуру в проект.
     *
     * @param texture Добавляемая текстура.
     */
    void addTexture(Texture texture);

    /**
     * Удаляет текстуру из проекта.
     *
     * @param i Номер текстуры для удаления.
     */
    void removeTexture(int i);
}

/**
 * Класс, реализующий бизнес-логику редактора 3D графики.
 */
class EditorBusinessLogicalLayer implements BusinessLogicalLayer {

    private DatabaseAccess databaseAccess;

    public EditorBusinessLogicalLayer(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    @Override
    public Collection<Model3D> getAllModels() {
        return databaseAccess.getAllModels();
    }

    @Override
    public Collection<Texture> getAllTextures() {
        return databaseAccess.getAllTextures();
    }

    @Override
    public void renderModel(Model3D model) {
        processRender(model);
    }

    @Override
    public void renderAllModels() {
        for (Model3D model : getAllModels()) {
            processRender(model);
        }
    }

    @Override
    public void addModel(Model3D model) {
        databaseAccess.addModel(model);
    }

    @Override
    public void removeModel(Model3D model) {
        databaseAccess.removeModel(model);
    }

    @Override
    public void addTexture(Texture texture) {
        databaseAccess.addTexture(texture);
    }

    @Override
    public void removeTexture(Texture texture) {
        databaseAccess.removeTexture(texture);
    }

    private Random random = new Random();

    private void processRender(Model3D model) {
        try {
            Thread.sleep(2500 - random.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Интерфейс Business Logical Layer (BLL).
 */
interface BusinessLogicalLayer {

    /**
     * Получает все модели проекта.
     *
     * @return Список всех моделей проекта.
     */
    Collection<Model3D> getAllModels();

    /**
     * Получает все текстуры проекта.
     *
     * @return Список всех текстур проекта.
     */
    Collection<Texture> getAllTextures();

    /**
     * Выполняет рендер указанной модели.
     *
     * @param model Модель для рендеринга.
     */
    void renderModel(Model3D model);

    /**
     * Выполняет рендер всех моделей проекта.
     */
    void renderAllModels();

    /**
     * Добавляет 3D модель в проект.
     *
     * @param model Добавляемая модель.
     */
    void addModel(Model3D model);

    /**
     * Удаляет 3D модель из проекта.
     *
     * @param model Удаляемая модель.
     */
    void removeModel(Model3D model);

    /**
     * Добавляет текстуру в проект.
     *
     * @param texture Добавляемая текстура.
     */
    void addTexture(Texture texture);

    /**
     * Удаляет текстуру из проекта.
     *
     * @param texture Удаляемая текстура.
     */
    void removeTexture(Texture texture);
}

/**
 * Класс, реализующий доступ к базе данных редактора 3D графики.
 */
class EditorDatabaseAccess implements DatabaseAccess {

    private final Database editorDatabase;

    public EditorDatabaseAccess(Database editorDatabase) {
        this.editorDatabase = editorDatabase;
    }

    @Override
    public Collection<Model3D> getAllModels() {
        Collection<Model3D> models = new ArrayList<>();
        for (Entity entity : editorDatabase.getAll()) {
            if (entity instanceof Model3D) {
                models.add((Model3D) entity);
            }
        }
        return models;
    }

    @Override
    public void addModel(Model3D model) {

    }

    @Override
    public void removeModel(Model3D model) {

    }

    @Override
    public void addTexture(Texture texture) {

    }

    @Override
    public void removeTexture(Texture texture) {

    }

    @Override
    public Collection<Texture> getAllTextures() {
        Collection<Texture> textures = new ArrayList<>();
        for (Entity entity : editorDatabase.getAll()) {
            if (entity instanceof Texture) {
                textures.add((Texture) entity);
            }
        }
        return textures;
    }

    @Override
    public void addEntity(Entity entity) {
        editorDatabase.addEntity(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        editorDatabase.removeEntity(entity);
    }
}

/**
 * Интерфейс Database Access (DAC).
 */
interface DatabaseAccess {

    /**
     * Добавляет сущность в базу данных.
     *
     * @param entity Добавляемая сущность.
     */
    void addEntity(Entity entity);

    /**
     * Удаляет сущность из базы данных.
     *
     * @param entity Удаляемая сущность.
     */
    void removeEntity(Entity entity);

    /**
     * Получает все текстуры из базы данных.
     *
     * @return Список всех текстур.
     */
    Collection<Texture> getAllTextures();

    /**
     * Получает все модели из базы данных.
     *
     * @return Список всех моделей.
     */
    Collection<Model3D> getAllModels();
    /**
     * Добавляет 3D модель в базу данных.
     *
     * @param model Добавляемая 3D модель.
     */
    void addModel(Model3D model);

    /**
     * Удаляет 3D модель из базы данных.
     *
     * @param model Удаляемая 3D модель.
     */
    void removeModel(Model3D model);
    /**
     * Добавляет текстуру в базу данных.
     *
     * @param texture Добавляемая текстура.
     */
    void addTexture(Texture texture);

    /**
     * Удаляет текстуру из базы данных.
     *
     * @param texture Удаляемая текстура.
     */
    void removeTexture(Texture texture);
}

/**
 * Класс, представляющий базу данных редактора 3D графики.
 */
class EditorDatabase implements Database {

    private Random random = new Random();
    private final ProjectFile projectFile;
    private Collection<Entity> entities;

    public EditorDatabase(ProjectFile projectFile) {
        this.projectFile = projectFile;
        load();
    }

    @Override
    public void load() {
        //TODO: Загрузка всех сущностей проекта (модели, текстуры и т. д)
    }

    @Override
    public void save() {
        //TODO: Сохранение текущего состояния всех сущностей проекта
    }

    public Collection<Entity> getAll() {
        if (entities == null) {
            entities = new ArrayList<>();
            int entCount = random.nextInt(5, 11);
            for (int i = 0; i < entCount; i++) {
                generateModelAndTextures();
            }
        }
        return entities;
    }
    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    private void generateModelAndTextures() {
        Model3D model3D = new Model3D();
        int txCount = random.nextInt(3);
        for (int i = 0; i < txCount; i++) {
            Texture texture = new Texture();
            model3D.getTextures().add(texture);
            entities.add(texture);
        }
        entities.add(model3D);
    }
}

/**
 * Интерфейс Database (БД).
 */
interface Database {

    /**
     * Загружает данные из базы данных.
     */
    void load();

    /**
     * Сохраняет текущее состояние в базе данных.
     */
    void save();

    /**
     * Получает все сущности из базы данных.
     *
     * @return Список всех сущностей.
     */
    Collection<Entity> getAll();

    void addEntity(Entity entity);
    void removeEntity(Entity entity);
}

/**
 * Класс, представляющий 3D модель.
 */
class Model3D implements Entity {

    private static int counter = 10000;
    private int id;
    private String name;
    private Collection<Texture> textures = new ArrayList<>();

    @Override
    public int getId() {
        return id;
    }

    {
        id = ++counter;
    }

    public Model3D() {
    }

    public Model3D(String name) {
        this.name = name;
    }

    public Collection<Texture> getTextures() {
        return textures;
    }

    @Override
    public String toString() {
        return String.format("3DModel #%s: %s", id, name);
    }
}

/**
 * Класс, представляющий текстуру.
 */
class Texture implements Entity {

    private static int counter = 50000;
    private int id;
    private String name;

    {
        id = ++counter;
    }

    @Override
    public int getId() {
        return id;
    }

    public Texture() {
    }

    public Texture(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Texture #%s: %s", id, name);
    }
}

/**
 * Интерфейс Entity (Сущность).
 */
interface Entity {

    int getId();
}

/**
 * Класс, представляющий файл проекта.
 */
class ProjectFile {

    private String fileName;
    private int setting1;
    private String setting2;
    private boolean setting3;

    public ProjectFile(String fileName) {

        this.fileName = fileName;
        //TODO: Загрузка настроек проекта из файла

        setting1 = 1;
        setting2 = "...";
        setting3 = false;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSetting1() {
        return setting1;
    }

    public String getSetting2() {
        return setting2;
    }

    public boolean getSetting3() {
        return setting3;
    }
}