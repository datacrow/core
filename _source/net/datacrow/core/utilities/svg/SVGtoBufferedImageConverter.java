/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.core.utilities.svg;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

public class SVGtoBufferedImageConverter{
   
    public SVGtoBufferedImageConverter() {}
   
    public BufferedImage renderSVG(String strFileName) throws Exception {
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        BufferedImage outputImage = null;
        transcode(strFileName, transcoder);
        outputImage = transcoder.getLastRendered();
        return outputImage;
    }
   
    private void transcode(String inputFile, BufferedImageTranscoder transcoder) throws Exception {
        InputStream in = new FileInputStream(inputFile);
        TranscoderInput input = new TranscoderInput(in);
        TranscoderOutput output = new TranscoderOutput();
        transcoder.transcode(input, output);
        in.close();
    }
}
