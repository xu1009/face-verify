package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceOperate;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.cloud.http.Response;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import vo.Result;
import vo.Status;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Controller
public class FaceController implements InitializingBean{

	private CommonOperate commonOperate;
	private FaceOperate faceOperate;
	private FaceSetOperate faceSetOperate;

	private static final Logger LOGGER = Logger.getLogger(FaceController.class);

	@Value("${key}")
	private String apiKey;

	@Value("${screct}")
	private String apiSecret;

	@Value("${faceSetId}")
	private String faceSetId;

	@Value("${imagePath}")
	private String imagePath;


	@RequestMapping("/addFace.do")
	@ResponseBody
	public Status addFace(HttpServletRequest request, String data, String id){
		LOGGER.info("this is new request!!!!");
		Status status = new Status();
		status.setStatus(1);
		String base64 = data.replaceAll(" ", "+");
		String tempPath = imagePath + id + "_add" + ".jpg";
		tempPath = GenerateImage(base64, tempPath);
		try {
			BufferedImage src = ImageIO.read(new File(tempPath));
			if (src.getWidth() > src.getHeight()){
				BufferedImage des = RotateImage.Rotate(src, 90);
				ImageIO.write(des, "jpg", new File(tempPath));
				base64 = GetImageStr(tempPath);
			}
			Response response = commonOperate.detectBase64(base64, 0, null);
			if (response.getStatus() != 200){
				status.setStatus(0);
				return status;
			}
			LOGGER.info(new String(response.getContent()));
			String faceToken = getFaceToken(response);
			Response response1 = faceOperate.faceSetUserId(faceToken, id);
			if (response1.getStatus() != 200){
				status.setStatus(0);
				return status;
			}
			LOGGER.info(new String(response1.getContent()));
			Response response2 = faceSetOperate.createFaceSet(null,faceSetId,null,faceToken,null, 1);
			if (response2.getStatus() != 200){
				status.setStatus(0);
				return status;
			}
			LOGGER.info(new String(response2.getContent()));
		} catch (Exception e) {
			LOGGER.error("no face !!!!!!!!!!!");
			e.printStackTrace();
			status.setStatus(0);
		}
		return status;
	}

	@RequestMapping("/verifyFace.do")
	@ResponseBody
	public Result verifyFace(HttpServletRequest request, String data, String id){
		LOGGER.info("this is new req!!!");
		Result result = new Result();
		result.setStatus(true);
		String base64 = data.replaceAll(" ", "+");
		String tempPath = imagePath + "tst" + "_verify" + ".jpg";
		tempPath = GenerateImage(base64, tempPath);
		try {
			BufferedImage src = ImageIO.read(new File(tempPath));
			if (src.getWidth() > src.getHeight()){
				BufferedImage des = RotateImage.Rotate(src, 90);
				ImageIO.write(des, "jpg", new File(tempPath));
				LOGGER.info("get it!!!!!!!");
				base64 = GetImageStr(tempPath);
			}
			Response response = commonOperate.searchByOuterId(base64, null, null, faceSetId, 1);
			if (response.getStatus() != 200){
				result.setStatus(false);
				return result;
			}
			String jsonRes = new String(response.getContent());
			ObjectMapper mapper = new ObjectMapper();
			Map map = mapper.readValue(jsonRes, Map.class);
			List results = (List) map.get("results");
			if (results == null){
				result.setStatus(false);
				return result;
			}
			LOGGER.info(jsonRes);
			Map map1 = (Map) results.get(0);
			String userid = (String) map1.get("user_id");
			double confidence = (double) map1.get("confidence");
			if (confidence > 70.0){
				result.setUsername(userid);
			}
			LOGGER.info("username: " + userid + "   " + "confidence:  " + confidence);
		} catch (Exception e) {
			e.printStackTrace();
			result.setStatus(false);
		}
		return result;
	}



	@Override
	public void afterPropertiesSet() throws Exception {
		commonOperate = new CommonOperate(apiKey, apiSecret, false);
		faceOperate = new FaceOperate(apiKey, apiSecret, false);
		faceSetOperate = new FaceSetOperate(apiKey, apiSecret, false);
	}


	private String getFaceToken(Response response) throws JSONException {
		if(response.getStatus() != 200){
			return new String(response.getContent());
		}
		String res = new String(response.getContent());
		JSONObject json = new JSONObject(res);
		String faceToken = json.optJSONArray("faces").optJSONObject(0).optString("face_token");
		return faceToken;
	}
	private String GenerateImage(String imgStr, String imagePath)
	{
		if (imgStr == null) {
			return null;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try
		{
			byte[] b = decoder.decodeBuffer(imgStr);
			for(int i=0;i<b.length;++i)
			{
				if(b[i]<0)
				{
					b[i]+=256;
				}
			}
			if (new File(imagePath).exists()){
				Random random = new Random();
				String prefixPath = imagePath.split("\\.")[0];
				prefixPath = prefixPath + random.nextInt(1000);
				imagePath = prefixPath + ".jpg";
			}
			OutputStream out = new FileOutputStream(imagePath);
			out.write(b);
			out.flush();
			out.close();
			return imagePath;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	private static String GetImageStr(String imgFile)
	{
		InputStream in = null;
		byte[] data = null;
		try
		{
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}



}
