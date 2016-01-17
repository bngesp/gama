/**
* Name: Roaster File in a Grid of cells
* Author: Maroussia Vavasseur and Benoit Gaudou
* Description: Model which shows how to import a Roaster file in GAMA and use it to initialize the color attributes of a grid of cells.
* 	This model represents a grid with a cell, each cell has a color, and this color is used for animals (izards) to be placed on a cell
* 	that verifies some conditions. The izards do not move once they have been placed. 
* Tags : :  Import Files, GIS
*/

model HowToImportRaster

global {	
	// Constants 
	const heightImg type: int <- 5587;
	const widthImg type: int <- 6201;	 
	  
	// Global variables
	int factorDiscret <- 30 ;
	file mntImageRaster <- image_file('../images/mnt/testAG.jpg') ;
	
	int nbIzard <- 250 ; 
	file izardShape <- file('../images/icons/izard.gif');
			
	// Initialization of grid and creation of the izard agents
	// - we use the as_matrix operator to transform a image file into a matrix of colors 
	// (Note that as_matrix takes a Point as right operand, this point specifies the number of lines and columns of the matrix)
	// - we then set the color built-in attribute of the cell with the value of the corresponding matrix cell     
	init {
		// set mapColor value: mntImageRaster as_matrix {widthImg/factorDiscret,heightImg/factorDiscret} ;
		ask cell {		
			color <-rgb( (mntImageRaster) at {grid_x,grid_y}) ;
		}
		create izard number: nbIzard; 
    }
}
 

// We create izard agents and locate them on one'cell' among the list of cellules that verifies the following conditions : is empty ('empty(each.agents)') 
// and with a color that is not white 'each.color != #white'
// the shuffle operator is used to randomized the list of cells


species izard {	
	init{
		location <- (shuffle(cell) first_with ((each.color != #white) and (empty(agents overlapping each)))).location ;
	}		
	aspect default{
		draw square(1) color: #orange;
	}
	aspect image{
		draw izardShape size: 3;
	}
}

// We create a grid as environment with the same dimensions as the matrix in which we want to store the image
// Note that the height (resp. the width) of the grid corresponds to the number of rows (resp. of columns) of the matrix:
// - in the creation of a matrix: ([...] as_matrix {widthImg/factorDiscret,heightImg/factorDiscret} ;)
// - in the creation of the grid: grid cellule width: widthImg/factorDiscret height: heightImg/factorDiscret;

grid cell  width: widthImg/factorDiscret height: heightImg/factorDiscret;



experiment main type: gui {	
	// We display:
	// - the grid
	// - the original MNT image as background
	// - izard agents
	// We can thus compare the original MNT image and the discretized image in the grid.
	// For cosmetic need, we can choose to not display the grid. 
	output {
		display HowToImportRaster {
	       grid cell;
	       image 'Background' file: mntImageRaster.path;
	       species izard aspect: image; 
	    }   
	}	
}
