/**
 * Created by Vasundhara Gupta November 14,2014
 */
package GameObject;

import java.io.Serializable;

/**
 * Defines the data contained in each of the 50 stages present in the game. For this purpose
 * we keep a static object containing the StageData for the 60 stages of the game (10 bonus
 * stages are also included).
 */
public class Stages implements Serializable {

	// An array used to contain the data for all 60 stages present in the game.
	// Note that the first element is null, so that we can index the stages from
	// 1.
	public static final StageData[] gameStages = {
			null,

			new StageData( //stage 1
					new EnemySet[] { new EnemySet(EnemyType.BALLOOM, 6) },
					PowerUpType.FLAMES, false),

			//stage 2
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM, 3),
					new EnemySet(EnemyType.ONEAL, 3) },
                    PowerUpType.BOMBS, false),

			//stage 3
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM, 2),
					new EnemySet(EnemyType.ONEAL, 2),
					new EnemySet(EnemyType.DOLL, 2) },
                    PowerUpType.DETONATOR, false),

			//stage 4
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM, 1),
					new EnemySet(EnemyType.ONEAL, 1),
					new EnemySet(EnemyType.DOLL, 2),
					new EnemySet(EnemyType.MINVO, 2) },
                    PowerUpType.SPEED, false),

			//stage 5
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL, 4),
					new EnemySet(EnemyType.DOLL, 3) },
                    PowerUpType.BOMBS, false),

            //bonus stage A
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM, 1) },
                    null, true),

			//stage 6
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL, 2),
					new EnemySet(EnemyType.DOLL, 3),
					new EnemySet(EnemyType.MINVO, 2) },
                    PowerUpType.BOMBS, false),

			//stage 7
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL, 2),
					new EnemySet(EnemyType.DOLL, 3),
					new EnemySet(EnemyType.KONDORIA, 2) },
                    PowerUpType.FLAMES, false),

			//stage 8
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL, 1),
					new EnemySet(EnemyType.DOLL, 2),
					new EnemySet(EnemyType.MINVO, 4) },
                    PowerUpType.DETONATOR, false),

			//stage 9
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL, 1),
					new EnemySet(EnemyType.DOLL, 1),
					new EnemySet(EnemyType.MINVO, 4),
					new EnemySet(EnemyType.KONDORIA, 1) },
					PowerUpType.BOMBPASS, false),

			//stage 10
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.OVAPI,1 ) },
					PowerUpType.WALLPASS, false),

			//bonus stage B
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL, 1) },
					null, true),

			//stage 11
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,1 ),
					new EnemySet(EnemyType.OVAPI,1 ) },
					PowerUpType.BOMBS, false),

			//stage 12
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,4 ),
					new EnemySet(EnemyType.OVAPI,1 ) },
					PowerUpType.BOMBS, false),

			//stage 13
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,3 ),
					new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,3) },
					PowerUpType.DETONATOR, false),

			//stage 14
			new StageData(new EnemySet[] { new EnemySet(EnemyType.OVAPI,7 ),
					new EnemySet(EnemyType.PASS,1 ) },
					PowerUpType.BOMBPASS, false),

			//stage 15
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,3),
					new EnemySet(EnemyType.PASS,1 )},
					PowerUpType.FLAMES, false),

			//bonus stage C
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL, 1) },
					null, true),

			//stage 16
			new StageData(new EnemySet[] { new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,4 ),
					new EnemySet(EnemyType.PASS,1 )},
					PowerUpType.WALLPASS, false),

			//stage 17
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,5 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.PASS,1 )},
					PowerUpType.BOMBS, false),

			//stage 18
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM,3 ),
					new EnemySet(EnemyType.ONEAL,3 ),
					new EnemySet(EnemyType.PASS,2 )},
					PowerUpType.BOMBPASS, false),

			//stage 19
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM,1 ),
					new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,3 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,2) },
					PowerUpType.BOMBS, false),

			//stage 20
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,2 )},
					PowerUpType.DETONATOR, false),

			//bonus stage D
			new StageData(new EnemySet[] { new EnemySet(EnemyType.MINVO, 1) },
					null, true),

			//stage 21
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,4 ),
					new EnemySet(EnemyType.OVAPI,3 ),
					new EnemySet(EnemyType.PASS,2 ) },
					PowerUpType.BOMBPASS, false),

			//stage 22
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,4 ),
					new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,1 ),
					new EnemySet(EnemyType.PASS,1 )},
					PowerUpType.DETONATOR, false),

			//stage 23
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,2 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,1 ) },
					PowerUpType.BOMBS, false),

			//stage 24
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,4 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,1 ) },
					PowerUpType.DETONATOR, false),

			//stage 25
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,2 ),
					new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,1 )},
					PowerUpType.BOMBPASS, false),

			//bonus Stage E
			new StageData(new EnemySet[] { new EnemySet(EnemyType.OVAPI, 1) },
					null, true),

			//stage 26
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM,1 ),
					new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,1 )},
					PowerUpType.MYSTERY, false),

			//stage 27
			new StageData(new EnemySet[] { new EnemySet(EnemyType.BALLOOM,1 ),
					new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.KONDORIA,5),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,1 ) },
					PowerUpType.FLAMES, false),

			//stage 28
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,3 ),
					new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,1 ),
					new EnemySet(EnemyType.PASS,1 ) },
					PowerUpType.BOMBS, false),

			//stage 29
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,5 ),
					new EnemySet(EnemyType.PASS,2 ) },
					PowerUpType.DETONATOR, false),

			//stage 30
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,3 ),
					new EnemySet(EnemyType.MINVO,2 ),
					new EnemySet(EnemyType.KONDORIA,1 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,1 ) },
					PowerUpType.FLAMEPASS, false),

			//bonus stage F
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA, 1) },
					null, true),

			//stage 31
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,2 ),
					new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,2 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,2 ) },
					PowerUpType.WALLPASS, false),

			//stage 32
			new StageData(new EnemySet[] { new EnemySet(EnemyType.ONEAL,1 ),
					new EnemySet(EnemyType.DOLL,1),
					new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,4 ),
					new EnemySet(EnemyType.PASS,1 ) },
					PowerUpType.BOMBS, false),

			//stage 33
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,2 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,2 ) },
					PowerUpType.DETONATOR, false),

			//stage 34
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,3 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.PASS,2 ) },
					PowerUpType.MYSTERY, false),

			//stage 35
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,2 ) },
					PowerUpType.BOMBPASS, false),

			//bonus stage G
			new StageData(new EnemySet[] { new EnemySet(EnemyType.PASS, 1) },
					null, true),

			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,2 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.PASS,3 )},
					PowerUpType.FLAMEPASS, false),

			//stage 37
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,3 ) },
					PowerUpType.DETONATOR, false),

			//stage 38
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,2 ),
					new EnemySet(EnemyType.MINVO,2 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.PASS,3 )},
					PowerUpType.FLAMES, false),

			//stage 39
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,1),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,4 ) },
					PowerUpType.WALLPASS, false),

			//Bonus stage H
			new StageData(new EnemySet[] { new EnemySet(EnemyType.PONTAN, 1) },
					null, true),

			//stage 40
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,2),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.PASS,4 ) },
					PowerUpType.MYSTERY, false),

			//Stage 41
			new StageData(new EnemySet[] { new EnemySet(EnemyType.DOLL,1 ),
					new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,4 ) },
					PowerUpType.DETONATOR, false),

			//Stage 42
			new StageData(new EnemySet[] { new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,3 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,5 ) },
					PowerUpType.WALLPASS, false),

			//Stage 43
			new StageData(new EnemySet[] { new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,6 )},
					PowerUpType.BOMBPASS, false),

			//Stage 44
			new StageData(new EnemySet[] { new EnemySet(EnemyType.MINVO,1 ),
					new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,6 )},
					PowerUpType.DETONATOR, false),

			//Bonus stage I
			new StageData(new EnemySet[] { new EnemySet(EnemyType.PONTAN,1) },
					null, true),

			//Stage 45
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,6 )},
					PowerUpType.MYSTERY, false),

			//Stage 46
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,6 )},
					PowerUpType.WALLPASS, false),

			//Stage 47
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,6 )},
					PowerUpType.BOMBPASS, false),

			//Stage 48
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,2 ),
					new EnemySet(EnemyType.OVAPI,1 ),
					new EnemySet(EnemyType.PASS,6 ),
					new EnemySet(EnemyType.PONTAN,1 ) },
					PowerUpType.DETONATOR, false),

			//Stage 49
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,1 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,6 ),
					new EnemySet(EnemyType.PONTAN,1 ) },
					PowerUpType.FLAMEPASS, false),

			//bonus stage J
			new StageData(new EnemySet[] { new EnemySet(EnemyType.PONTAN, 1) },
					null, true),

			//Stage 50
			new StageData(new EnemySet[] { new EnemySet(EnemyType.KONDORIA,1 ),
					new EnemySet(EnemyType.OVAPI,2 ),
					new EnemySet(EnemyType.PASS,5 ),
					new EnemySet(EnemyType.PONTAN,2 ) },
					PowerUpType.MYSTERY, false),
	};

}
