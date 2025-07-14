import java.util.Scanner;
import java.util.Random;

public class ray {
    static String map =
        "11111111111111111111111111111111" +
        "10000000000000000000000000000001" +
        "10111111111111111100000000000001" +
        "10000000000000000100000000000001" +
        "10000000000000000100000000000001" +
        "10000000000000000100000000000001" +
        "10000000000000000100000000000001" +
        "10000000000000000111111111111101" +
        "10000000000000000000000000000001" +
        "10000000000000000000000000000001" +
        "10000000000000000000000000000001" +
        "10000000000000000000000000000001" +
        "10000000000000000000000000000001" +
        "10000000000000000000000000000001" +
        "10000000000000000000000000000001" +
        "11111111111111111111111111111111";
/*
#define MAP_WIDTH     32
#define MAP_HEIGHT    16
#define FOV           3.14159f / 4.0f
#define DEPTH         16.0f
#define SCREEN_WIDTH  209
#define SCREEN_HEIGHT 51
*/
    public static final int MAP_WIDTH = 32;
    public static final int MAP_HEIGHT = 16;
    public static final float FOV = (float) (3.14159 / 4.0);
    public static final float DEPTH = 16.0f;
    public static final int SCREEN_WIDTH = 209;
    public static final int SCREEN_HEIGHT = 51;

    public float playerX = 2.0f, playerY = 2.0f;
    public float playerA = 0.0f;


/*
void moveXY(int x, int y, char *print) {
    COORD coord = {x, y};
    SetConsoleCursorPosition(GetStdHandle(STD_OUTPUT_HANDLE), coord);
    printf(print);
}
*/
    public static void setCursorPosition(int x, int y) {
        // ANSI escape code: \033[y;xH
        System.out.print(String.format("\033[%d;%dH", y, x));
        System.out.flush();
    }


/*
void halfwaybullets () {
    for (int i = (SCREEN_WIDTH/2)-10; i >= 0; i--) {
        printf(" ");
    }
}
*/
    public static void halfwaybullets() {
        for (int i = (SCREEN_WIDTH / 2) - 10; i >= 0; i--) {
            System.out.print(" ");
        }
    }

    //didnt wriute this someone on stadck overflow did I had no idea on why my escape code wherent working
    public static void enableAnsi() {
    try {
        // Enable ANSI escape codes 
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", ""); // Dummy to open cmd
            pb.redirectErrorStream(true);
            Process p = pb.start();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    //main
    public static void main(String[] args) throws Exception {
        ray game = new ray();
        game.run();
    }

    public void run() throws Exception {
        

        //for come god forsaken reason this langage has this 
        Scanner scanner = new Scanner(System.in);

        //prompt to start 
        System.out.print("Hit the key you want to use then hit enter to move around \n\tAre you ready to start? Y/n - ");
        char startLetter = scanner.nextLine().trim().toLowerCase().charAt(0);

        //check what was taken in
        if (startLetter == 'y') {
            char[] screen = new char[SCREEN_WIDTH * SCREEN_HEIGHT];

            //inti screen buffer with spaces
            for (int i = 0; i < screen.length; i++)
                screen[i] = ' ';

            Random random = new Random();


            // gameloop
        while (true) {
                //java is so slow that you can vibily see it writing from top to bottom not a issue I had with C so I am adding that it draws and then waits until a key is pressed then keeps going
                // clear screen
                System.out.print("\033[H\033[2J");
                System.out.flush();

                // ray cast and puyt the drawing inot the screen buffer (idk if it is even a buffer this is my first java program trying to turn something I made in C)
                for (int x = 0; x < SCREEN_WIDTH; x++) {
                    //start raycasting
                    float rayAngle = (playerA - FOV / 2.0f) + ((float) x / (float) SCREEN_WIDTH) * FOV;

                    float distanceToWall = 0;
                    boolean hitWall = false;
                    float eyeX = (float) Math.sin(rayAngle);
                    float eyeY = (float) Math.cos(rayAngle);

                    // if hitting wall ore inside of wall
                    while (!hitWall && distanceToWall < DEPTH) {
                        distanceToWall += 0.1f;
                        int testX = (int) (playerX + eyeX * distanceToWall);
                        int testY = (int) (playerY + eyeY * distanceToWall);

                        if (testX < 0 || testX >= MAP_WIDTH || testY < 0 || testY >= MAP_HEIGHT) {
                            hitWall = true;
                            distanceToWall = DEPTH;
                        } else if (map.charAt(testY * MAP_WIDTH + testX) == '1') {
                            hitWall = true;
                        }
                    }


                    int ceiling = (int) ((SCREEN_HEIGHT / 2.0f) - SCREEN_HEIGHT / distanceToWall);
                    int floor = SCREEN_HEIGHT - ceiling;

                    for (int y = 0; y < SCREEN_HEIGHT; y++) {
                        int idx = y * SCREEN_WIDTH + x;
                        //ray cast ceiling
                        if (y < ceiling) {
                            screen[idx] = ' ';
                        } else if (y >= ceiling && y <= floor) { //ray cast walls
                            if (distanceToWall <= DEPTH / 4.0f) screen[idx] = '\u2588';
                            else if (distanceToWall < DEPTH / 3.0f) screen[idx] = '\u2593';
                            else if (distanceToWall < DEPTH / 2.0f) screen[idx] = '\u2592';
                            else if (distanceToWall < DEPTH) screen[idx] = '\u2591';
                            else screen[idx] = ' ';
                        } else { //ray cast floor 
                            float b = 1.0f - (((float) y - SCREEN_HEIGHT / 2.0f) / (SCREEN_HEIGHT / 2.0f));
                            if (b < 0.25) screen[idx] = '#';
                            else if (b < 0.5) screen[idx] = 'x';
                            else if (b < 0.75) screen[idx] = '.';
                            else if (b < 0.9) screen[idx] = '-';
                            else screen[idx] = ' ';
                        }
                    }
                }

                //print the screen buffer to console
                for (int y = 0; y < SCREEN_HEIGHT; y++) {
                    for (int x = 0; x < SCREEN_WIDTH; x++) {
                        System.out.print(screen[y * SCREEN_WIDTH + x]);
                    }
                    System.out.println();
                }

                //input handling fdor moving around
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    char key = Character.toLowerCase(line.charAt(0));
                    if (key == 'a') playerA -= 0.1f;
                    if (key == 'd') playerA += 0.1f;
                    if (key == 'w') {
                        playerX += Math.sin(playerA) * 0.5f;
                        playerY += Math.cos(playerA) * 0.5f;
                        if (map.charAt((int) playerY * MAP_WIDTH + (int) playerX) == '1') {
                            playerX -= Math.sin(playerA) * 0.5f;
                            playerY -= Math.cos(playerA) * 0.5f;
                        }
                    }
                    if (key == 's') {
                        playerX -= Math.sin(playerA) * 0.5f;
                        playerY -= Math.cos(playerA) * 0.5f;
                        if (map.charAt((int) playerY * MAP_WIDTH + (int) playerX) == '1') {
                            playerX += Math.sin(playerA) * 0.5f;
                            playerY += Math.cos(playerA) * 0.5f;
                        }
                    }
                    if (key == 'q') break;
                }
            }
        }
        scanner.close();
    }
}



 /*
int main() {
    char startLetter = 
    printf("are you ready to start? Y/n - ");
    scanf(" %c", &startLetter);

    //make the screen buffer
    wchar_t *screen = malloc(sizeof(wchar_t) * SCREEN_WIDTH * SCREEN_HEIGHT);
    for (int i = 0; i < SCREEN_WIDTH * SCREEN_HEIGHT; i++)
        screen[i] = L' ';

    HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
    COORD bufferSize = { SCREEN_WIDTH, SCREEN_HEIGHT };
    SetConsoleScreenBufferSize(hConsole, bufferSize);
    SetConsoleActiveScreenBuffer(hConsole);

    if (startLetter == 'Y' || startLetter == 'y') {
        // spawn enemies
        srand(time(NULL));
        enemyCount = 5;
        for (int i = 0; i < enemyCount; i++) {
            enemies[i] = spawnEnemy();
            while (map[(int)enemies[i].yCords * MAP_WIDTH + (int)enemies[i].xCords] == '1') {
                enemies[i] = spawnEnemy();
            }
        }

        //draw weapoin before raycasting
        drawWeapon_norm(gunFire, gunArrow);

        while (1) {
            //get input
            if (_kbhit()) {
                char key = _getch();
                if (key == leftKey) playerA -= 0.1f;
                if (key == rightKey) playerA += 0.1f;
                if (key == forwardKey) {
                    playerX += sinf(playerA) * 0.5f;
                    playerY += cosf(playerA) * 0.5f;
                    if (map[(int)playerY * MAP_WIDTH + (int)playerX] == '1') {
                        playerX -= sinf(playerA) * 0.5f;
                        playerY -= cosf(playerA) * 0.5f;
                    }
                }
                if (key == backKey) {
                    playerX -= sinf(playerA) * 0.5f;
                    playerY -= cosf(playerA) * 0.5f;
                    if (map[(int)playerY * MAP_WIDTH + (int)playerX] == '1') {
                        playerX += sinf(playerA) * 0.5f;
                        playerY += cosf(playerA) * 0.5f;
                    }
                }

                if (key == fireKey) {
                    gunArrow = false;
                }

                if (key == menuKey) {
                    openMenu();
                }

                if (key == reloadKey) {
                    if (canReload) {
                        gunArrow = true;
                    }
                }
            }

            //raycast and render into the screen buffer
            for (int x = 0; x < SCREEN_WIDTH; x++) {
                float rayAngle = (playerA - FOV / 2.0f) + ((float)x / (float)SCREEN_WIDTH) * FOV;

                float distanceToWall = 0;
                int hitWall = 0;
                float eyeX = sinf(rayAngle);
                float eyeY = cosf(rayAngle);

                while (!hitWall && distanceToWall < DEPTH) {
                    distanceToWall += 0.1f;
                    int testX = (int)(playerX + eyeX * distanceToWall);
                    int testY = (int)(playerY + eyeY * distanceToWall);

                    if (testX < 0 || testX >= MAP_WIDTH || testY < 0 || testY >= MAP_HEIGHT) {
                        hitWall = 1;
                        distanceToWall = DEPTH;
                    } else if (map[testY * MAP_WIDTH + testX] == '1') {
                        hitWall = 1;
                    }
                }

                int ceiling = (float)(SCREEN_HEIGHT / 2.0f) - SCREEN_HEIGHT / ((float)distanceToWall);
                int floor = SCREEN_HEIGHT - ceiling;

                for (int y = 0; y < SCREEN_HEIGHT; y++) {
                    int idx = y * SCREEN_WIDTH + x;

                    if (y < ceiling) {
                        screen[idx] = L' ';
                    } else if (y >= ceiling && y <= floor) {
                        if (distanceToWall <= DEPTH / 4.0f)       screen[idx] = 0x2588;
                        else if (distanceToWall < DEPTH / 3.0f)  screen[idx] = 0x2593;
                        else if (distanceToWall < DEPTH / 2.0f)  screen[idx] = 0x2592;
                        else if (distanceToWall < DEPTH)         screen[idx] = 0x2591;
                        else                                     screen[idx] = L' ';
                    } else {
                        float b = 1.0f - (((float)y - SCREEN_HEIGHT / 2.0f) / (SCREEN_HEIGHT / 2.0f));
                        if (b < 0.25)      screen[idx] = '#';
                        else if (b < 0.5)  screen[idx] = 'x';
                        else if (b < 0.75) screen[idx] = '.';
                        else if (b < 0.9)  screen[idx] = '-';
                        else               screen[idx] = ' ';
                    }
                }

                // draw enemies
                for (int i = 0; i < enemyCount; i++) {
                    float dx = enemies[i].xCords - playerX;
                    float dy = enemies[i].yCords - playerY;
                    float distanceToEnemy = sqrtf(dx * dx + dy * dy);
                    float enemyAngle = atan2f(dx, dy) - playerA;

                    if (fabs(enemyAngle) < FOV / 2.0f && distanceToEnemy < distanceToWall) {
                        int enemyScreenX = (int)((enemyAngle + FOV / 2.0f) / FOV * SCREEN_WIDTH);

                        for (int row = 0; row < ENEMY_HEIGHT; row++) {
                            int drawY = SCREEN_HEIGHT / 2 - ENEMY_HEIGHT / 2 + row;
                            if (drawY < 0 || drawY >= SCREEN_HEIGHT) continue;

                            for (int col = 0; col < ENEMY_WIDTH; col++) {
                                int drawX = enemyScreenX - ENEMY_WIDTH / 2 + col;
                                if (drawX < 0 || drawX >= SCREEN_WIDTH) continue;

                                char ch = enemySprite[row][col];
                                if (ch != ' ') {
                                    screen[drawY * SCREEN_WIDTH + drawX] = ch;
                                }
                            }
                        }
                    }
                }
            }

            DWORD dwBytesWritten = 0;
            WriteConsoleOutputCharacterW(hConsole, screen, SCREEN_WIDTH * SCREEN_HEIGHT, (COORD){0,0}, &dwBytesWritten);

            if (turnsSinceGunFire >= 0)
                turnsSinceGunFire++;

            if (turnsSinceGunFire >= 15) {
                turnsSinceGunFire = 1;
            }

            drawWeapon_norm(gunFire, gunArrow);
            Sleep(60);
        }
    }

    free(screen);
    return 0;
}

        */
