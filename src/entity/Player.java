package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;

public class Player extends Entity {
	
	GamePanel gp;
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	public int hasKey = 0;
	int standCounter = 0;
	
	
	public Player(GamePanel gp, KeyHandler keyH) {
		
		this.gp = gp;
		this.keyH = keyH;
		
		screenX = gp.screenWidth/2 - (gp.tileSize/2);
		screenY = gp.screenHeight/2 - (gp.tileSize/2);
		
		solidArea = new Rectangle();
		solidArea.x = 16;
		solidArea.y = 16;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		solidArea.width = 16;
		solidArea.height = 16;
		//8 16 32 32
		
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		
		worldX = gp.tileSize * 24;
		worldY = gp.tileSize * 24;
		speed = 5;
		direction = "down";
	}
	
	public void getPlayerImage() {
		
		up1 = setup("player_up1");
		up2 = setup("player_up2");
		down1 = setup("player_down1");
		down2 = setup("player_down2");
		left1 = setup("player_left1");
		left2 = setup("player_left2");
		right1 = setup("player_right1");
		right2 = setup("player_right2");
	}
	
	public BufferedImage setup(String imageName) {
		
		UtilityTool uTool = new UtilityTool();
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/player/" + imageName + ".png"));
			image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
	public void update() {
		
		if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
		
			if (keyH.upPressed == true) {
				direction = "up";
			}
			else if (keyH.downPressed == true) {
				direction = "down";
			}
			else if (keyH.leftPressed == true) {
				direction = "left";
			}
			else if (keyH.rightPressed == true) {
				direction = "right";
			}
			
			
			//collision check calls
			collisionOn = false;
			gp.cCheck.checkTile(this);
			
			//object collision
			int objIndex = gp.cCheck.checkObject (this, true);
			pickUpObject(objIndex);
			
			
			if (collisionOn == false) {
				
				switch(direction) {
				case "up": worldY -= speed; break;
				case "down": worldY += speed; break;
				case "left": worldX -= speed; break;
				case "right": worldX += speed; break;
				}
			}
		
			spriteCounter++;
			if (spriteCounter > 30) {
				if (spriteNum == 1) {
					spriteNum = 2;
				}
				else if (spriteNum == 2) {
					spriteNum = 1;
				}
				spriteCounter = 0;
			}
		}
		else {
			standCounter++;
			
			if (standCounter == 20) {
				spriteNum = 1;
				standCounter = 0;
			}
		}
	}
	
	public void pickUpObject (int i) {
		
		if (i != 999) {
			
			String objectName = gp.obj[i].name;
			
			switch (objectName) {
			
			case "Key":
				gp.playSE(2);
				hasKey++;
				gp.obj[i] = null;
				gp.ui.showMessage("Key obtained!");
				break;
				
			case "Door":
				if (hasKey > 0) {
					gp.playSE(3);
					gp.obj[i] = null;
					hasKey--;
					gp.ui.showMessage("Door Opened!");
				}
				else {
					gp.ui.showMessage("No Key!");
				}
				break;
				
			case "Chest":
				gp.playSE(4);
				gp.obj[i] = null;
				gp.ui.gameFinished = true;
				gp.stopMusic();
				gp.playSE(1);
				break;
				
			case "Boots":
				gp.playSE(1);
				speed += 2;
				gp.obj[i] = null;
				gp.ui.showMessage("+ 2 Speed!");
				break;
				
			}
		}
	}
	
	public void draw (Graphics2D g2) {
		
		//g2.setColor(Color.white);
		//g2.fillRect(x, y, gp.tileSize, gp.tileSize);
		
		
		
		BufferedImage image = null;
		
		switch (direction) {
		case "up":
			if (spriteNum == 1) {
				image = up1;
			}
			if (spriteNum == 2) {
				image = up2;
			}
			break;
		case "down":
			if (spriteNum == 1) {
				image = down1;
			}
			if (spriteNum == 2) {
				image = down2;
			}
			break;
		case "left":
			if (spriteNum == 1) {
				image = left1;
			}
			if (spriteNum == 2) {
				image = left2;
			}
			break;
		case "right":
			if (spriteNum == 1) {
				image = right1;
			}
			if (spriteNum == 2) {
				image = right2;
			}
			break;
		}
		
		g2.drawImage(image, screenX, screenY, null);
		
		if (gp.debugMode == true) {
			g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
		}
	}

}
