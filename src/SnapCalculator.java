import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.VersionChecker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class SnapCalculator {
    public static void calcIndex(String filePath) {
        try {
            //BeamLogManager.removeRootLoggerHandlers();
            System.out.println("\nLoading product: " + filePath);
            Product product = ProductIO.readProduct(filePath);
            String productType = product.getProductType();

            System.out.println(productType);

            boolean isOlci = productType.matches("org.esa.s3tbx.c2rcc.olci.C2rccOlciOperator");
            boolean isMeris = productType.matches("org.esa.s3tbx.c2rcc.meris.C2rccMerisOperator");


            if (isOlci || isMeris) {
                //ProductIO.readProduct()

                int rasterWidth = product.getSceneRasterWidth();
                int rasterHeight = product.getSceneRasterHeight();

                //Product targetProduct = product;
                //Product targetProduct = new Product(new File(filePath + "_test.dim").getName(), "MER_FR__2P", rasterWidth, rasterHeight);

                ProductWriter productWriter = ProductIO.getProductWriter("BEAM-DIMAP");
                //ProductReader productReader = ProductIO.getProductReaderForInput(filePath);
                //targetProduct.setProductWriter(productWriter);
                //product.setProductReader(productReader);
                product.setProductWriter(productWriter);
                //productWriter.flush();

                productWriter.writeProductNodes(product, "out/" + filePath);

                //System.out.println(productWriter.getOutput());

                /*Band[] bands = product.getBands();

                for (int i = 0; i < bands.length; i++) {
                    System.out.println(bands[i].getName());
                    //System.out.print(".");
                }*/

                //String[] bandNames = product.getBandNames();

                Band r400Band = null;
                Band r412Band = null;
                Band r442Band = null;
                Band r490Band = null;
                Band r510Band = null;
                Band r560Band = null;
                Band r620Band = null;
                Band r665Band = null;
                Band r673Band = null;
                Band r681Band = null;
                Band r708Band = null;
                Band r753Band = null;

                if (isOlci) {
                    r400Band = product.getBand("rhow_1");
                    r412Band = product.getBand("rhow_2");
                    r442Band = product.getBand("rhow_3");
                    r490Band = product.getBand("rhow_4");
                    r510Band = product.getBand("rhow_5");
                    r560Band = product.getBand("rhow_6");
                    r620Band = product.getBand("rhow_7");
                    r665Band = product.getBand("rhow_8");
                    r673Band = product.getBand("rhow_9");
                    r681Band = product.getBand("rhow_10");
                    r708Band = product.getBand("rhow_11");
                    r753Band = product.getBand("rhow_12");
                    //r778Band = product.getBand("rhow_16");
                    //r865Band = product.getBand("rhow_17");
                    //r885Band = product.getBand("rhow_18");
                    //r1020Band = product.getBand("rhow_21");
                } else if (isMeris) {
                    r412Band = product.getBand("rhow_1");
                    r442Band = product.getBand("rhow_2");
                    r490Band = product.getBand("rhow_3");
                    r510Band = product.getBand("rhow_4");
                    r560Band = product.getBand("rhow_5");
                    r620Band = product.getBand("rhow_6");
                    r665Band = product.getBand("rhow_7");
                    r681Band = product.getBand("rhow_8");
                    r708Band = product.getBand("rhow_9");
                    r753Band = product.getBand("rhow_10");
                    //r778Band = product.getBand("rhow_12");
                    //r865Band = product.getBand("rhow_13");
                }

                Band algal2Band = product.getBand("conc_chl");
                Band yellowSubsBand = product.getBand("iop_adg");
                Band totalSuspBand = product.getBand("conc_tsm");
                //Band l2Flags = product.getBand("l2_flags");
                Raster r400 = isOlci ? r400Band.getSourceImage().getData() : null;
                Raster r412 = r412Band.getSourceImage().getData();
                Raster r442 = r442Band.getSourceImage().getData();
                Raster r490 = r490Band.getSourceImage().getData();
                Raster r510 = r510Band.getSourceImage().getData();
                Raster r560 = r560Band.getSourceImage().getData();
                Raster r620 = r620Band.getSourceImage().getData();
                Raster r665 = r665Band.getSourceImage().getData();
                Raster r673 = isOlci ? r673Band.getSourceImage().getData(): null;
                Raster r681 = r681Band.getSourceImage().getData();
                Raster r708 = r708Band.getSourceImage().getData();
                Raster r753 = r753Band.getSourceImage().getData();
                Raster algal_2 = algal2Band.getSourceImage().getData();
                Raster yellow_subs = yellowSubsBand.getSourceImage().getData();
                Raster total_susp = totalSuspBand.getSourceImage().getData();
                //productWriter.writeBandRasterData(l2Flags, 0, 0, rasterWidth, rasterHeight, product, ProgressMonitor.NULL);

                String maskNames[] = product.getMaskGroup().getNodeNames();

                /*for (int i = 0; i < maskNames.length; i++) {
                    System.out.println(maskNames[i]);
                }*/

                Mask landMask = isOlci ? product.getMaskGroup().get("quality_flags_land") :
                        product.getMaskGroup().get("land");
                Mask freshInlandWaterMask = isOlci ? product.getMaskGroup().get("quality_flags_fresh_inland_water") : null;
                //Mask bpacOnMask = product.getMaskGroup().get("bpac_on");
                //Mask uncertainAerosolModelMask = product.getMaskGroup().get("uncertain_aerosol_model");

            /*for (int i = 0; i < product.getMaskGroup().getNodeCount(); i++){
                System.out.println(product.getMaskGroup().getNodeDisplayNames()[i]);
            }*/

                Raster landMaskData = landMask.getSourceImage().getData();
                Raster freshInlandWaterMaskData = isOlci ? freshInlandWaterMask.getSourceImage().getData() : null;
                //Raster bpacOnMaskData = bpacOnMask.getSourceImage().getData();
                //Raster uncertainAerosolModelMaskData = uncertainAerosolModelMask.getSourceImage().getData();

                //System.out.println(waterMask.getDescription());
                //System.out.println(waterMask.getDisplayName());

                //double[] data = new double[rasterWidth];

                Band[] bands = product.getBands();
                for (int i = 0; i < bands.length; i++) {
                    //System.out.println(bands[i].getName());
                    //System.out.print(".");
                    //System.out.println(bandNames[i]);
                    //ProductUtils.copyBand(bandNames[i], product, targetProduct, true);
                    //bands[i].setSourceImage(product.getBand(bands[i].getName()).getSourceImage());
                    //bands[i].fireProductNodeDataChanged();
                    //if (bands[i].getName().equals("l2_flags")) {
                        System.out.print(".");
                        bands[i].writeRasterDataFully();
                    /*} else {
                        product.removeBand(bands[i]);
                    }*/
                }

                Band kd490 = new Band("kd_490", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band CY = new Band("CY", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band MCI = new Band("MCI", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band aPig = new Band("a_pig", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band c490 = new Band("c_490", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band secchiPhot = new Band("Secchi_phot", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band secchiBS = new Band("Secchi_BS", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band couplingConstant = new Band("coupling_constant", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band secchiPhotRefl = new Band("Secchi_phot_refl", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlMCIPeipsi = new Band("Chl_MCI_Peipsi", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlMCIVorts = new Band("Chl_MCI_Vorts", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band FBMMCIPeipsi = new Band("FBM_MCI_Peipsi", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band FBMMCIVorts = new Band("FBM_MCI_Vorts", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band CYMCIPeipsi = new Band("CY_MCI_Peipsi", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band CYMCIVorts = new Band("CY_MCI_Vorts", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band CYPeipsisuvi = new Band("CY_Peipsi_suvi", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigPV = new Band("Chl_apig_PV", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigP = new Band("Chl_apig_P", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigV = new Band("Chl_apig_V", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigPVkevad = new Band("Chl_apig_PV_kevad", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigPVsuvi = new Band("Chl_apig_PV_suvi", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigPavaosa = new Band("Chl_apig_P_avaosa", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigLaPi = new Band("Chl_apig_LaPi", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlapigPi = new Band("Chl_apig_Pi", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlMCIL2 = new Band("Chl_MCI_L2", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band chlBinding = new Band("chl_binding", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                Band aTot = new Band("a_tot", ProductData.TYPE_FLOAT64, rasterWidth, rasterHeight);
                product.addBand(kd490);
                product.addBand(CY);
                product.addBand(MCI);
                product.addBand(aPig);
                product.addBand(c490);
                product.addBand(secchiPhot);
                product.addBand(secchiBS);
                product.addBand(couplingConstant);
                product.addBand(secchiPhotRefl);
                product.addBand(chlMCIPeipsi);
                product.addBand(chlMCIVorts);
                product.addBand(FBMMCIPeipsi);
                product.addBand(FBMMCIVorts);
                product.addBand(CYMCIPeipsi);
                product.addBand(CYMCIVorts);
                product.addBand(CYPeipsisuvi);
                product.addBand(chlapigPV);
                product.addBand(chlapigP);
                product.addBand(chlapigV);
                product.addBand(chlapigPVkevad);
                product.addBand(chlapigPVsuvi);
                product.addBand(chlapigPavaosa);
                product.addBand(chlapigLaPi);
                product.addBand(chlapigPi);
                product.addBand(chlMCIL2);
                product.addBand(chlBinding);
                product.addBand(aTot);

                productWriter.writeProductNodes(product, "out/" + filePath);

                //ProductUtils.copyTiePointGrids(product, targetProduct);
                //ProductUtils.copyGeoCoding(product, targetProduct);
                //ProductUtils.copyMetadata(product, targetProduct);


                //ProductUtils.copyBand("l2_flags", product, targetProduct, true);


                //productWriter.writeProductNodes(product, "test.dim");

                //targetBand.setSourceImage(product.getBand("l2_flags").getSourceImage());
                //targetBand.writePixels(0, 0, targetBand.getSceneRasterWidth(), targetBand.getSceneRasterHeight(), product.getBand("l2_flags").getSourceImage().getData());


                double[] olciConstants = {0.0004, 0.0012, 0.023, 0.208, 0.503, 0.995, 0.381, 0.061, 0.032, 0.017, 0.0021, 0.0001};
                double[] merisConstants = {0.0012, 0.023, 0.208, 0.503, 0.995, 0.381, 0.061, 0.017, 0.0021, 0.0001};
                double[] constants = isOlci ? olciConstants : merisConstants;
                double constantSum = 0;

                for (int k = 0; k < constants.length; k++) {
                    constantSum += constants[k];
                }

                for (int y = 0; y < rasterHeight; y++) {

                    if (y % 100 == 0) {
                        System.out.print(":");
                    }

                    double[] r400row = new double[rasterWidth];
                    double[] r412row = new double[rasterWidth];
                    double[] r442row = new double[rasterWidth];
                    double[] r490row = new double[rasterWidth];
                    double[] r510row = new double[rasterWidth];
                    double[] r560row = new double[rasterWidth];
                    double[] r620row = new double[rasterWidth];
                    double[] r665row = new double[rasterWidth];
                    double[] r673row = new double[rasterWidth];
                    double[] r681row = new double[rasterWidth];
                    double[] r708row = new double[rasterWidth];
                    double[] r753row = new double[rasterWidth];
                    double[] algal2row = new double[rasterWidth];
                    int[] yellowSubsRow = new int[rasterWidth];
                    double[] totalSuspRow = new double[rasterWidth];

                    r412.getPixels(0, y, rasterWidth, 1, r412row);
                    r442.getPixels(0, y, rasterWidth, 1, r442row);
                    r490.getPixels(0, y, rasterWidth, 1, r490row);
                    r510.getPixels(0, y, rasterWidth, 1, r510row);
                    r560.getPixels(0, y, rasterWidth, 1, r560row);
                    r620.getPixels(0, y, rasterWidth, 1, r620row);
                    r665.getPixels(0, y, rasterWidth, 1, r665row);
                    r681.getPixels(0, y, rasterWidth, 1, r681row);
                    r708.getPixels(0, y, rasterWidth, 1, r708row);
                    r753.getPixels(0, y, rasterWidth, 1, r753row);
                    algal_2.getPixels(0, y, rasterWidth, 1, algal2row);
                    yellow_subs.getPixels(0, y, rasterWidth, 1, yellowSubsRow);
                    total_susp.getPixels(0, y, rasterWidth, 1, totalSuspRow);

                    if (isOlci) {
                        r400.getPixels(0, y, rasterWidth, 1, r400row);
                        r673.getPixels(0, y, rasterWidth, 1, r673row);
                    }

                    //System.out.println("r442row " + r442row[0]);

                    double[] kd490row = new double[rasterWidth];
                    double[] CYrow = new double[rasterWidth];
                    double[] MCIrow = new double[rasterWidth];
                    double[] aPigRow = new double[rasterWidth];
                    double[] c490row = new double[rasterWidth];
                    double[] secchiPhotRow = new double[rasterWidth];
                    double[] secchiBSrow = new double[rasterWidth];
                    double[] couplingConstantRow = new double[rasterWidth];
                    double[] secchiPhotReflRow = new double[rasterWidth];
                    double[] chlMCIPeipsiRow = new double[rasterWidth];
                    double[] chlMCIVortsRow = new double[rasterWidth];
                    double[] FBMMCIPeipsiRow = new double[rasterWidth];
                    double[] FBMMCIVortsRow = new double[rasterWidth];
                    double[] CYMCIPeipsiRow = new double[rasterWidth];
                    double[] CYMCIVortsRow = new double[rasterWidth];
                    double[] CYPeipsisuviRow = new double[rasterWidth];
                    double[] chlapigPVRow = new double[rasterWidth];
                    double[] chlapigPRow = new double[rasterWidth];
                    double[] chlapigVRow = new double[rasterWidth];
                    double[] chlapigPVkevadRow = new double[rasterWidth];
                    double[] chlapigPVsuviRow = new double[rasterWidth];
                    double[] chlapigPavaosaRow = new double[rasterWidth];
                    double[] chlapigLaPiRow = new double[rasterWidth];
                    double[] chlapigPiRow = new double[rasterWidth];
                    double[] chlMCIL2Row = new double[rasterWidth];
                    double[] chlBindingRow = new double[rasterWidth];
                    double[] aTotRow = new double[rasterWidth];

                    double[] landMaskRow = new double[rasterWidth];
                    double[] freshInlandWaterMaskRow = new double[rasterWidth];
                    //double[] bpacOnMaskRow = new double[rasterWidth];
                    //double[] uncertainAerosolModelMaskRow = new double[rasterWidth];

                    landMaskData.getPixels(0, y, rasterWidth, 1, landMaskRow);

                    if (isOlci) {
                        freshInlandWaterMaskData.getPixels(0, y, rasterWidth, 1, freshInlandWaterMaskRow);
                    }

                    //bpacOnMaskData.getPixels(0, y, rasterWidth, 1, bpacOnMaskRow);
                    //uncertainAerosolModelMaskData.getPixels(0, y, rasterWidth, 1, uncertainAerosolModelMaskRow);

                    for (int x = 0; x < rasterWidth/* && x < 20*/; x++) {

                        double r400Pixel,
                                r412Pixel,
                                r442Pixel,
                                r490Pixel,
                                r510Pixel,
                                r560Pixel,
                                r620Pixel,
                                r665Pixel,
                                r673Pixel,
                                r681Pixel,
                                r708Pixel,
                                r753Pixel,
                                algal2Pixel,
                                yellowSubsPixel,
                                totalSuspPixel;

                    /*double[] kd490pixel = new double[1];
                    double[] CYpixel = new double[1];
                    double[] MCIpixel = new double[1];
                    double[] aPigPixel = new double[1];
                    double[] c490pixel = new double[1];
                    double[] secchiPhotPixel = new double[1];
                    double[] secchiBSpixel = new double[1];*/



                    /*double[] waterMaskPixel = new double[1];
                    double[] bpacOnMaskPixel = new double[1];
                    double[] uncertainAerosolModelMaskPixel = new double[1];

                    waterMaskData.getPixel(x, y, waterMaskPixel);
                    bpacOnMaskData.getPixel(x, y, bpacOnMaskPixel);
                    uncertainAerosolModelMaskData.getPixel(x, y, uncertainAerosolModelMaskPixel);*/



                        //System.out.println(waterMaskRow[x] + " " + bpacOnMaskRow[x] + " " + uncertainAerosolModelMaskRow[x]);
                        //System.out.println(landMaskRow[x]);
                        //if (waterMaskRow[x] > 0.0 && bpacOnMaskRow[x] > 0.0 && uncertainAerosolModelMaskRow[x] == 0.0) {
                        if (landMaskRow[x] < 255.0 || freshInlandWaterMaskRow[x] > 0.0) {
                            r400Pixel = isOlci ? r400Band.scale(r400row[x]) : 0;
                            r412Pixel = r412Band.scale(r412row[x]);
                            r442Pixel = r442Band.scale(r442row[x]);
                            r490Pixel = r490Band.scale(r490row[x]);
                            r510Pixel = r510Band.scale(r510row[x]);
                            r560Pixel = r560Band.scale(r560row[x]);
                            r620Pixel = r620Band.scale(r620row[x]);
                            r665Pixel = r665Band.scale(r665row[x]);
                            r673Pixel = isOlci ? r673Band.scale(r673row[x]) : 0;
                            r681Pixel = r681Band.scale(r681row[x]);
                            r708Pixel = r708Band.scale(r708row[x]);
                            r753Pixel = r753Band.scale(r753row[x]);
                            algal2Pixel = algal2Band.scale(algal2row[x]);
                            yellowSubsPixel = yellowSubsBand.scale(yellowSubsRow[x]);
                            totalSuspPixel = totalSuspBand.scale(totalSuspRow[x]);

                            //System.out.println(r442row[x]);

                            kd490row[x] = calcKd490(r490Pixel, r560Pixel, r708Pixel);
                            aPigRow[x] = calcAPig(algal2Pixel);
                            c490row[x] = calcC490(algal2Pixel, yellowSubsPixel, totalSuspPixel);
                            CYrow[x] = calcCY(r665Pixel, r681Pixel, r708Pixel);
                            MCIrow[x] = calcMCI(r681Pixel, r708Pixel, r753Pixel);
                            secchiPhotRow[x] = calcSecchiPhot(c490row[x], kd490row[x]);
                            secchiBSrow[x] = calcSecchiBS(kd490row[x]);

                            double photopocReflectance = 0;

                            double[] olciReflPixels = {
                                    r400Pixel,
                                    r412Pixel,
                                    r442Pixel,
                                    r490Pixel,
                                    r510Pixel,
                                    r560Pixel,
                                    r620Pixel,
                                    r665Pixel,
                                    r673Pixel,
                                    r681Pixel,
                                    r708Pixel,
                                    r753Pixel
                            };

                            double[] merisReflPixels = {
                                    r412Pixel,
                                    r442Pixel,
                                    r490Pixel,
                                    r510Pixel,
                                    r560Pixel,
                                    r620Pixel,
                                    r665Pixel,
                                    r681Pixel,
                                    r708Pixel,
                                    r753Pixel
                            };

                            double[] reflPixels = isOlci ? olciReflPixels : merisReflPixels;

                            for (int p = 0; p < reflPixels.length; p++) {
                                photopocReflectance += constants[p] * reflPixels[p];
                            }

                            photopocReflectance /= constantSum;

                            chlMCIPeipsiRow[x] = calcChlMCIPeipsi(MCIrow[x]);
                            chlMCIVortsRow[x] = calcChlMCIVorts(MCIrow[x]);
                            FBMMCIPeipsiRow[x] = calcFBMMCIPeipsi(MCIrow[x]);
                            FBMMCIVortsRow[x] = calcFBMMCIVorts(MCIrow[x]);
                            CYMCIPeipsiRow[x] = calcCYMCIPeipsi(MCIrow[x]);
                            CYMCIVortsRow[x] = calcCYMCIVorts(MCIrow[x]);
                            CYPeipsisuviRow[x] = calcCYPeipsiSuvi(MCIrow[x]);
                            chlapigPVRow[x] = calcChlapigPV(aPigRow[x]);
                            chlapigPRow[x] = calcChlapigP(aPigRow[x]);
                            chlapigVRow[x] = calcChlapigV(aPigRow[x]);
                            chlapigPVkevadRow[x] = calcChlapigPVkevad(aPigRow[x]);
                            chlapigPVsuviRow[x] = calcChlapigPVsuvi(aPigRow[x]);
                            chlapigPavaosaRow[x] = calcChlapigPavaosa(aPigRow[x]);
                            chlapigLaPiRow[x] = calcChlapigLaPi(aPigRow[x]);
                            chlapigPiRow[x] = calcChlapigPi(aPigRow[x]);
                            chlMCIL2Row[x] = calcMCIL2(MCIrow[x]);
                            chlBindingRow[x] = calcChlBinding(MCIrow[x]);
                            aTotRow[x] = aPigRow[x] + yellowSubsPixel;

                            couplingConstantRow[x] = calcCouplingConstant(photopocReflectance);
                            secchiPhotReflRow[x] = calcSecchiPhotRefl(
                                    kd490row[x], totalSuspPixel, algal2Pixel, couplingConstantRow[x]
                            );

                        } else {
                            kd490row[x] = Double.NaN;
                            aPigRow[x] = Double.NaN;
                            c490row[x] = Double.NaN;
                            CYrow[x] = Double.NaN;
                            MCIrow[x] = Double.NaN;
                            secchiPhotRow[x] = Double.NaN;
                            secchiBSrow[x] = Double.NaN;
                            chlMCIPeipsiRow[x] = Double.NaN;
                            chlMCIVortsRow[x] = Double.NaN;
                            FBMMCIPeipsiRow[x] = Double.NaN;
                            FBMMCIVortsRow[x] = Double.NaN;
                            CYMCIPeipsiRow[x] = Double.NaN;
                            CYMCIVortsRow[x] = Double.NaN;
                            CYPeipsisuviRow[x] = Double.NaN;
                            chlapigPVRow[x] = Double.NaN;
                            chlapigPRow[x] = Double.NaN;
                            chlapigVRow[x] = Double.NaN;
                            chlapigPVkevadRow[x] = Double.NaN;
                            chlapigPVsuviRow[x] = Double.NaN;
                            chlapigPavaosaRow[x] = Double.NaN;
                            chlapigLaPiRow[x] = Double.NaN;
                            chlapigPiRow[x] = Double.NaN;
                            chlMCIL2Row[x] = Double.NaN;
                            chlBindingRow[x] = Double.NaN;
                            aTotRow[x] = Double.NaN;
                        }

                        //System.out.println(aPigRow[x] + " " + yellowSubsBand.scale(yellowSubsRow[x]) + " " + totalSuspBand.scale(totalSuspRow[x]));
                        //System.out.println(c490row[x]);
                        //System.out.println(r442row[x] + " " + r510row[x] + " " + r673row[x]);
                        //System.out.println(kd490row[x]);

                        //kd490.writePixels(x, y, rasterWidth, 1, data);
                    /*kd490.writePixels(x, y, 1, 1, kd490pixel);
                    c490.writePixels(x, y, 1, 1, c490pixel);
                    CY.writePixels(x, y, 1, 1, CYpixel);
                    MCI.writePixels(x, y, 1, 1, MCIpixel);
                    aPig.writePixels(x, y, 1, 1, aPigPixel);
                    secchiPhot.writePixels(x, y, 1, 1, secchiPhotPixel);
                    secchiBS.writePixels(x, y, 1, 1, secchiBSpixel);*/
                    }

                    kd490.writePixels(0, y, rasterWidth, 1, kd490row);
                    c490.writePixels(0, y, rasterWidth, 1, c490row);
                    CY.writePixels(0, y, rasterWidth, 1, CYrow);
                    MCI.writePixels(0, y, rasterWidth, 1, MCIrow);
                    aPig.writePixels(0, y, rasterWidth, 1, aPigRow);
                    secchiPhot.writePixels(0, y, rasterWidth, 1, secchiPhotRow);
                    secchiBS.writePixels(0, y, rasterWidth, 1, secchiBSrow);
                    chlMCIPeipsi.writePixels(0, y, rasterWidth, 1, chlMCIPeipsiRow);
                    chlMCIVorts.writePixels(0, y, rasterWidth, 1, chlMCIVortsRow);
                    FBMMCIPeipsi.writePixels(0, y, rasterWidth, 1, FBMMCIPeipsiRow);
                    FBMMCIVorts.writePixels(0, y, rasterWidth, 1, FBMMCIVortsRow);
                    CYMCIPeipsi.writePixels(0, y, rasterWidth, 1, CYMCIPeipsiRow);
                    CYMCIVorts.writePixels(0, y, rasterWidth, 1, CYMCIVortsRow);
                    CYPeipsisuvi.writePixels(0, y, rasterWidth, 1, CYPeipsisuviRow);
                    chlapigPV.writePixels(0, y, rasterWidth, 1, chlapigPVRow);
                    chlapigP.writePixels(0, y, rasterWidth, 1, chlapigPRow);
                    chlapigV.writePixels(0, y, rasterWidth, 1, chlapigVRow);
                    chlapigPVkevad.writePixels(0, y, rasterWidth, 1, chlapigPVkevadRow);
                    chlapigPVsuvi.writePixels(0, y, rasterWidth, 1, chlapigPVsuviRow);
                    chlapigPavaosa.writePixels(0, y, rasterWidth, 1, chlapigPavaosaRow);
                    chlapigLaPi.writePixels(0, y, rasterWidth, 1, chlapigLaPiRow);
                    chlapigPi.writePixels(0, y, rasterWidth, 1, chlapigPiRow);
                    chlMCIL2.writePixels(0, y, rasterWidth, 1, chlMCIL2Row);
                    chlBinding.writePixels(0, y, rasterWidth, 1, chlBindingRow);
                    aTot.writePixels(0, y, rasterWidth, 1, aTotRow);
                    couplingConstant.writePixels(0, y, rasterWidth, 1, couplingConstantRow);
                    secchiPhotRefl.writePixels(0, y, rasterWidth, 1, secchiPhotReflRow);
                }

                //kd_490.writeRasterDataFully();
                //kd_490.setSourceImage(kd_490.getSourceImage());
                //System.out.println(kd_490.isSourceImageSet());
                //ProductIO.writeProduct(product, filePath + "_test.dim", ProductIO.DEFAULT_FORMAT_NAME);

                //targetProduct.dispose();

                //BeamLogManager.removeRootLoggerHandlers(); // get rid of BEAM console logging
                //ImageInfo imageInfo = ProductUtils.createImageInfo(rgbBands, true, ProgressMonitor.NULL);
                //BufferedImage image = ProductUtils.createRgbImage(rgbBands, imageInfo, ProgressMonitor.NULL);
                //File imageFile = new File(product.getName() + ".png");
                //System.out.println("Writing RGB image: " + imageFile);
                //ImageIO.write(image, "PNG", imageFile);
                //System.out.println("RGB Image written: " + imageFile);
                //System.out.println("Product loaded: " + product.getName());
            }

            product.dispose();

        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static double calcKd490(double rhow4, double rhow6, double rhow11) {
        double weight = rhow6 / rhow11;
        double result = Double.NaN;
        double eq1;
        double eq2;

        if (weight < 0) {
            weight = 0;
        } else if (weight > 1) {
            weight = 1;
        }

        if (rhow4 >= 0 && rhow6 >= 0 && rhow11 >= 0) {
            eq1 = Math.exp(-0.9461 * (Math.log(rhow4 / rhow11)) + 0.4305) + 0.0166;
            eq2 = Math.exp(-1.1198 * (Math.log(rhow6 / rhow11)) + 1.4141) + 0.0166;
            result = ((1 - weight) * eq1) + (weight * eq2);
        }

        if (weight == 1 && rhow11 >= 0 && rhow6 >= 0) {
            result = Math.exp(-1.1198 * (Math.log(rhow6 / rhow11)) + 1.4141) + 0.0166;
        }

        if (rhow6 < 0 || rhow11 < 0) {
            result = Double.NaN;
        }

        return result;
    }

    private static double calcC490(double algal2, double yellowSubs, double totalSusp) {
        return ((Math.exp(Math.log(algal2 / 21) / 1.04)) * 0.67) + ((yellowSubs * 0.5) + 0.015) + ((totalSusp / 1.73 + 0.0049) * 0.95);
    }

    private static double calcCY(double rhow8, double rhow10, double rhow11) {
        return -1 * (rhow10 - rhow8 - (rhow11 - rhow8) * 0.36);
    }

    private static double calcMCI(double rhow10, double rhow11, double rhow12) {
        return rhow11 - rhow10 - 0.389 * (rhow12 - rhow10);
    }

    private static double calcAPig(double algal2) {
        return Math.exp(Math.log(algal2 / 21) / 1.04);
    }

    private static double calcSecchiPhot(double c490, double kd490) {
        return 8.35 / (0.7782 * (c490 + kd490) + 0.4132);
    }

    private static double calcSecchiBS(double kd490) {
        return 2.4 * Math.pow(kd490, -0.86);
    }

    private static double calcCouplingConstant(double photopocReflectance) {
        return Math.log(((0.82 - photopocReflectance) / photopocReflectance) / 0.0066);
    }

    private static double calcSecchiPhotRefl(double kd490, double totalSusp, double algal2, double couplingConstant) {
        double b490 = ((totalSusp / 1.73) + 0.00378163134957697) * 0.95;
        double aTot490 = ((Math.exp(Math.log(algal2 / 21) / 1.04))* 0.67) +(totalSusp * 0.5) + 0.015;

        return couplingConstant / (0.7782 * (b490 + aTot490 + kd490) + 0.4132);
    }

    private static double calcChlMCIPeipsi(double mci) {
        return 10.9 * mci + 15.3;
    }

    private static double calcChlMCIVorts(double mci) {
        return 6.8 * mci + 21.5;
    }

    private static double calcFBMMCIPeipsi(double mci) {
        return 5.8 * mci + 5.4;
    }

    private static double calcFBMMCIVorts(double mci) {
        return 6.7 * mci;
    }

    private static double calcCYMCIPeipsi(double mci) {
        return 4.9 * mci + 1.5;
    }

    private static double calcCYMCIVorts(double mci) {
        return 4.3 * mci;
    }

    private static double calcCYPeipsiSuvi(double mci) {
        return 4.6 * mci + 2.3;
    }

    private static double calcChlapigPV(double aPig) {
        return 30.15 * Math.pow(aPig, 0.530);
    }

    private static double calcChlapigP(double aPig) {
        return 29.66 * Math.pow(aPig, 0.503);
    }

    private static double calcChlapigV(double aPig) {
        return 27.1 * Math.pow(aPig, 0.802);
    }

    private static double calcChlapigPVkevad(double aPig) {
        return 27.91 * Math.pow(aPig, 0.825);
    }

    private static double calcChlapigPVsuvi(double aPig) {
        return 32.39 * Math.pow(aPig, 0.389);
    }

    private static double calcChlapigPavaosa(double aPig) {
        return 28.6 * Math.pow(aPig, 0.566);
    }

    private static double calcChlapigLaPi(double aPig) {
        return 15.614 * aPig + 20.57;
    }

    private static double calcChlapigPi(double aPig) {
        return 28.95 * Math.pow(aPig, 0.829);
    }

    private static double calcChlKop(double radiance7, double radiance9) {
        return 166 * (radiance9 / radiance7) - 106;
    }

    private static double calcMCIL2(double MCI) {
        return 1923.9 * MCI + 16.022;
    }

    private static double calcChlBinding(double MCI) {
        return 1457 * MCI + 2.895;
    }

    public static void createRgbImage(String filePath) {
        try {
            System.out.println("Loading product: " + filePath);
            Product product = ProductIO.readProduct(filePath);
            Band[] rgbBands = new Band[]{
                    product.getBand("radiance_13"),
                    product.getBand("radiance_4"),
                    product.getBand("radiance_2")
            };
            ImageInfo imageInfo = ProductUtils.createImageInfo(rgbBands, true, ProgressMonitor.NULL);
            BufferedImage image = ProductUtils.createRgbImage(rgbBands, imageInfo, ProgressMonitor.NULL);
            File imageFile = new File(product.getName() + ".png");
            System.out.println("Writing RGB image: " + imageFile);
            ImageIO.write(image, "PNG", imageFile);
            System.out.println("RGB Image written: " + imageFile);
            System.out.println("Product loaded: " + product.getName());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String test(String filePath) {
        try {
            Product product = ProductIO.readProduct(filePath);
            return "success";
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    public static String testSimple() {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String appName = SystemUtils.getApplicationName();
        try {
            String version = new VersionChecker().getLocalVersion();
            System.out.println(appName + " " + version);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}