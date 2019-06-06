package implementacoes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import swing.janelas.PDI_Lote;

/**
 * Classe para os métodos(funcoes) para o calculo do Greenness, onde serão mantidos
 * , que serao usadas pelo resto do programa.
 * 
 * @author Flavia Mattos & Arthur Costa
 */
public class Greenness {

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public int getLocalMean(int x, int y, BufferedImage img){
		
		int mean = 0;
		
		for(int i = x-1; i <= x+1 ; i++){
			for( int j = y-1; j <= y+1; j++){
				if((x-1)<1 || 
				   (y-1)<1 || 
				   (x+1)>=img.getWidth() || 
				   (y+1)>=img.getHeight() ) {
						Color aux = new Color(img.getRGB(x, y));
						mean += aux.getRed(); 
		//				mean += aux.getBlue();
		//				mean += aux.getGreen();
				}
				else {
					Color aux = new Color(img.getRGB(i, j));
					mean += aux.getRed();
		//			mean += aux.getBlue();
		//			mean += aux.getGreen();	This created a really neat style effect
				}
			}
		}
		mean /= 9 ;
		return mean;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public int getDeviation( int x, int y, BufferedImage img){

		int dev = 0;
		
		for(int i=x-1; i<=x+1; i++){
			for(int j=y-1; j<=y+1; j++){
				int localMean = this.getLocalMean(x, y, img);
				Color colorMean = new Color(localMean, localMean, localMean);
				
				if((x-1)<1 || 
				   (y-1)<1 || 
				   (x+1)>=img.getWidth() || 
				   (y+1)>=img.getHeight() ) {
						Color aux = new Color(img.getRGB(x, y));
						dev += Math.pow( aux.getRed() - colorMean.getRed(), 2 );	}
				
				else {
					Color aux = new Color(img.getRGB(i, j));
					dev += Math.pow( aux.getRed() - colorMean.getRed(), 2 );	}
				
			}
		}
		dev = (int) Math.sqrt(dev/9);
		return dev;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public int getGlobalMedian( int x, int y, BufferedImage img){

		int D = 0;
		Color aux;
		
		for( int i=0; i < img.getWidth(); i++){
			for( int j=0; j < img.getHeight(); j++){
				aux = new Color(img.getRGB(i, j));
				D += aux.getRed();	}
		}
		D /= ( img.getWidth()*img.getHeight() );
		return D;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	public int optimize(double k, int globalMean, int localDeviation, double b){
		return (int) ((k*globalMean)/(localDeviation+b));	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
/**
 * Essa função é a implementação da método de GreennesskG = kG − (R + B)
 * onde K é o valor passado pelo usuário e o R,G e B são os valores obtido da imagem
 * 
 * @param img A imagem onde o filtro será aplicado
 * @param K O valor K da equação
 * @return retorna a imagem depois de aplicado o filtro
 */
public BufferedImage GreennKG(BufferedImage img) {
    BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
    
    int globalMedian = this.getGlobalMedian(img.getWidth(), img.getHeight(), img);
    
    double a = 0;
    double b = 0;
    double c = 0;
    double k = 0.5;
    
    for(int i = 0; i<img.getWidth(); i++) {
    	for(int j = 0; j<img.getHeight(); j++) {
    		
    		Color aux = new Color(img.getRGB(i, j));
    		Color aux2 = new Color(aux.getRed(), aux.getRed(), aux.getRed());
    		
    		int localMean = this.getLocalMean(i, j, img);
    		Color colorMean = new Color(localMean, localMean, localMean);
    		
    		int deviation = this.getDeviation(i, j, img);
    		Color colorDeviation = new Color(deviation, deviation, deviation);
    		
    		int opt = this.optimize(k, globalMedian, colorDeviation.getRGB(), b);
    		Color colorOpt = new Color(opt, opt, opt);

    		
    		System.out.println(colorOpt.getRGB() + " = " + aux2.getRGB() + " = " + colorMean.getRGB());
    		
    		int enhance = (int) (colorOpt.getRed()*(aux2.getRed() - c*colorMean.getRed())+Math.pow(colorMean.getRed(), a));
    		//System.out.println(colorOpt.getRGB());
    		
    		//System.out.println(aux2.getRGB());
    		//System.out.println(colorMean.getRGB());
    		//System.out.println(enhance);
    		Color colorEnhance = new Color(enhance, enhance, enhance);
    		
    		res.setRGB(i, j, colorMean.getRGB());	
    		}
    }    
    return res;
}
















    
 /**
 * Essa função é a implementação da método de GreennessMin = G − min(R + B)
 * onde o R,G e B são os valores obtido da imagem
 * 
 * @param img A imagem onde o filtro será aplicado
 * @return retorna a imagem depois de aplicado o filtro
 */
    public BufferedImage GreennMin(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇAO
        double min = 10000;
        double max = 0;
        double menor ;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));
                
                if(x.getRed() < x.getBlue()){
                    menor = x.getRed();
                }else{
                    menor = x.getBlue();
                }
                
                double cor = x.getGreen() - menor;

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                
                Color p = new Color(img.getRGB(i, j));
                
                if(p.getRed() < p.getBlue()){
                    menor = p.getRed();
                }else {
                    menor = p.getBlue();
                }                
                
                
                double atual = p.getGreen() - menor;
                double cor = 255 * ((atual - min) / (max - min));

                int corB31 = (int) cor;

                Color novo = new Color(corB31, corB31, corB31);
                res.setRGB(i, j, novo.getRGB());
                
            }
        }
        return res;
    }
    
    /**
     * Essa função é a implementação da método de GreennessG−R = (G + R)/(G - R)
     * onde o R,G e B são os valores obtido da imagem
     * @param img A imagem onde o filtro será aplicado
     * @return retorna a imagem depois de aplicado o filtro
     */
    public BufferedImage GreennGmenR(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        double max = 0;
        int zero;
        double cor = 0;
        
        try {
            
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color x = new Color(img.getRGB(i, j));

                    zero = x.getGreen() + x.getRed();

                    if(zero != 0){

                        cor = (x.getGreen() - x.getRed()) / ((x.getGreen() + x.getRed())); //Oq fazer se tiver um pixel preto?

                    }else{

                        cor = 0;

                    }

                    if (cor < min) {
                        min = cor;
                    }
                    if (cor > max) {
                        max = cor;
                    }
                }
            }

            
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color p = new Color(img.getRGB(i, j));

                    zero = p.getGreen() + p.getRed();
                    if(zero != 0){

                        double atual = (p.getGreen() - p.getRed()) / ((p.getGreen() + p.getRed()));
                        cor = 255 * ((atual - min) / (max - min));

                    }else{

                        cor = 0;

                    }

                    int corB31 = (int) cor;

                    Color novo = new Color(corB31, corB31, corB31);
                    res.setRGB(i, j, novo.getRGB());
                }
            }
        } catch (java.lang.ArithmeticException e) {
            
            JOptionPane.showMessageDialog(null, "Divisão por Zero", "Error", 0);
            
        }
        //FINAL NORMALIZAÇÃO

        
    return res;
    }
    
    /**
     * Essa função é a implementação da método de GreennessG−R = (G − R)/(G + R)
     * onde K é o valor passado pelo usuário e o R,G e B são os valores obtido da imagem
     * @param img A imagem onde o filtro será aplicado
     * @return retorna a imagem depois de aplicado o filtro
     */
    public BufferedImage GreennGmaisR(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        int zero;
        double max = 0;
        double cor = 0;

        try {
            

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color x = new Color(img.getRGB(i, j));

                    zero = x.getGreen() - x.getRed();

                    if(zero != 0){

                        cor = (x.getGreen() + x.getRed() / (x.getGreen() - x.getRed()));

                    }else{

                        cor = 0;

                    }

                        if (cor < min) {
                            min = cor;
                        }
                        if (cor > max) {
                            max = cor;
                        }
                    }
            }
       
        //FINAL NORMALIZAÇÃO

       
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color p = new Color(img.getRGB(i, j));

                    zero = p.getGreen() - p.getRed();

                    if(zero != 0){

                            double atual = (p.getGreen() + p.getRed() / (p.getGreen() - p.getRed()));
                            cor = 255 * ((atual - min) / (max - min));

                    }else{

                        cor = 0;

                    }

                        int corB31 = (int) cor;

                        Color novo = new Color(corB31, corB31, corB31);
                        res.setRGB(i, j, novo.getRGB());
                }
            }
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Divisão por Zero", "Error", 0);
            
        }
        
        
    return res;
    }

    /**
     * Essa função é a implementação da método de Greenness = (G)/(R + G + B)
     * onde o R,G e B são os valores obtido da imagem
     * @param img
     * @return 
     */
    public BufferedImage Greenn(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        double max = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));

                double cor = x.getGreen() - (x.getRed() + x.getGreen() + x.getBlue());

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));

                double atual = p.getGreen() - (p.getRed() + p.getGreen() + p.getBlue() );

                double cor = 255 * ((atual - min) / (max - min));

                int corB32 = (int) cor;
                Color novo = new Color(corB32, corB32, corB32);
                res.setRGB(i, j, novo.getRGB());
            }
        }
        return res;
    }

    
    /**
     * Essa função é a implementação da método de GreennessSmolka = (G − Max{R,B})^2/G
     * onde o R,G e B são os valores obtido da imagem
     * @param img A imagem onde o filtro será aplicado
     * @return retorna a imagem depois de aplicado o filtro
     */
    public BufferedImage GreennSmolka(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        int zero;
        double max = 0;
        double cor = 0;
        double maior;

        try {
            

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color x = new Color(img.getRGB(i, j));

                    zero = x.getGreen();

                    if(zero != 0){

                        if(x.getRed() > x.getBlue()){
                            maior = x.getRed();
                        }else{
                            maior = x.getBlue();
                        }

                        cor = x.getGreen() - Math.pow((maior),9) / x.getGreen();

                    }else{

                        cor = 0;

                    }

                        if (cor < min) {
                            min = cor;
                        }
                        if (cor > max) {
                            max = cor;
                        }
                    }
            }
       
        //FINAL NORMALIZAÇÃO

       
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color p = new Color(img.getRGB(i, j));

                    zero = p.getGreen();

                    if(zero != 0){

                        if(p.getRed() > p.getBlue()){
                            maior = p.getRed();
                        }else{
                            maior = p.getBlue();
                        }

                        double atual = p.getGreen() - Math.pow(( maior),9) / p.getGreen();
                        cor = 255 * ((atual - min) / (max - min));
                        
                    }else{

                        cor = 0;

                    }

                            
                        int corB31 = (int) cor;

                        Color novo = new Color(corB31, corB31, corB31);
                        res.setRGB(i, j, novo.getRGB());
                }
            }
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Divisão por Zero", "Error", 0);
            
        }
        
    return res;
    }

    /**
    * Essa função é a implementação da método de GreennessG2 = (G^2 )/(B^2 + R^2 + k)
    * onde K é o valor passado pelo usuário e o R,G e B são os valores obtido da imagem
    * 
    * @param img A imagem onde o filtro será aplicado
    * @param K O valor K da equação
    * @return retorna a imagem depois de aplicado o filtro
    */
    public BufferedImage GreennG2(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇAO
        double min = 10000;
        double max = 0;
        double var = 14;
        double cor = 0;
        double maior;
        double zero;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                
                Color x = new Color(img.getRGB(i, j));
                
                zero = Math.pow(x.getBlue(),2) + Math.pow(x.getRed(),2) + var;

                if(zero != 0){

                    cor = Math.pow(x.getGreen(),2) / zero;

                }else{

                    cor = 0;

                }

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {

                Color p = new Color(img.getRGB(i, j));
                
                zero = Math.pow(p.getBlue(),2) + Math.pow(p.getRed(),2) + var;
                
                if(zero != 0){
                    
                    double atual = Math.pow(p.getGreen(),2) / zero;
                    cor = 255 * ((atual - min) / (max - min));

                }else{

                    cor = 0;

                }

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
                
                int corBK = (int) cor;

                Color novo = new Color(corBK, corBK, corBK);
                res.setRGB(i, j, novo.getRGB());
            }
        }
        return res;
    }
    
    /**
     * Essa função é a implementação da método de GreennessIPCA = I P CA = 0.7582|R − B| − 0.1168|R − G| + 0.6414|G − B|
     * onde o R,G e B são os valores obtido da imagem
     * @param img
     * @return 
     */
    public BufferedImage GreennIPCA(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        double max = 0;
        double RB, RG, GB;
        
        

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));
                
                RB = x.getRed() - x.getBlue();
                RG = x.getRed() - x.getGreen();
                GB = x.getGreen() - x.getBlue();

                //Colocar a função IPCA
                double cor = 0.7582*(Math.abs(RB)) - 0.1168*(Math.abs(RG)) + 0.6414*(Math.abs(GB));

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));

                RB = p.getRed() - p.getBlue();
                RG = p.getRed() - p.getGreen();
                GB = p.getGreen() - p.getBlue();
                
                double atual = 0.7582*(Math.abs(RB)) - 0.1168*(Math.abs(RG)) + 0.6414*(Math.abs(GB));

                double cor = 255 * ((atual - min) / (max - min));

                int corB32 = (int) cor;
                Color novo = new Color(corB32, corB32, corB32);
                res.setRGB(i, j, novo.getRGB());
            }
        }
        return res;
    }
    
    public BufferedImage BIEspacoX(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 100000;
        double max = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
               Color x = new Color(img.getRGB(i, j));
               
                //Conversão de RGB para Espaço de cor XYZ
                double Xx = x.getRed() * 0.4124 + x.getGreen() * 0.3575 + x.getBlue() * 0.1804;
                double Yx = x.getRed() * 0.2126 + x.getGreen() * 0.7156 + x.getBlue() * 0.0721;
                double Zx = x.getRed() * 0.0193 + x.getGreen() * 0.1191 + x.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Yx / 100) - 16);
                double a = 500 * ((Xx / 95.047) - (Yx / 100));
                double b = 200 * ((Yx / 100) - (Zx / 108.883));

                double P = (a + (1.75 * L)) / ((5.465 * L) + a - (3.012 * b));
                double cor = (100 * (P - 0.31)) / 0.17;

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));
                //Conversão de RGB para Espaço de cor XYZ
                double X = p.getRed() * 0.4124 + p.getGreen() * 0.3575 + p.getBlue() * 0.1804;
                double Y = p.getRed() * 0.2126 + p.getGreen() * 0.7156 + p.getBlue() * 0.0721;
                double Z = p.getRed() * 0.0193 + p.getGreen() * 0.1191 + p.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Y / 100) - 16);
                double a = 500 * ((X / 95.047) - (Y / 100));
                double b = 200 * ((Y / 100) - (Z / 108.883));

                double P = (a + 1.75 * L) / (5.465 * L + a - 3.012 * b);
                double atual = (100 * (P - 0.31)) / 0.17;

                double cor = 255 * ((atual - min) / (max - min));

                int corBX = (int) cor;

                Color novo = new Color(corBX, corBX, corBX);
                res.setRGB(i, j, novo.getRGB());

            }
        }
        return res;
    }

    public BufferedImage BIEspacoI(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 100000;
        double max = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));
                
                //Conversão de RGB para Espaço de cor XYZ
                double Xx = x.getRed() * 0.4124 + x.getGreen() * 0.3575 + x.getBlue() * 0.1804;
                double Yx = x.getRed() * 0.2126 + x.getGreen() * 0.7156 + x.getBlue() * 0.0721;
                double Zx = x.getRed() * 0.0193 + x.getGreen() * 0.1191 + x.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Yx / 1) - 16);

                double cor = 100 - L;

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));

                //Conversão de RGB para Espaço de cor XYZ
                double X = p.getRed() * 0.4124 + p.getGreen() * 0.3575 + p.getBlue() * 0.1804;
                double Y = p.getRed() * 0.2126 + p.getGreen() * 0.7156 + p.getBlue() * 0.0721;
                double Z = p.getRed() * 0.0193 + p.getGreen() * 0.1191 + p.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Y / 1) - 16);
                double atual = 100 - L;
                double cor = 255 * ((atual - min) / (max - min));

                int corBII = (int) cor;
                
                Color novo = new Color(corBII, corBII, corBII);
                res.setRGB(i, j, novo.getRGB());

            }
        }
        return res;
    }
}
