package com.yimeng.hyzchbczhwq.qrcode;


public class MainCodeActivity {
//	@ViewInject(R.id.tv_scan_result)
//	private TextView resultTextView;
//	@ViewInject(R.id.et_qr_string)
//	private EditText qrStrEditText;
//	@ViewInject(R.id.iv_qr_image)
//	private ImageView qrImgImageView;
//	@ViewInject(R.id.btn_scan_barcode)
//	private Button scButton;
//	@ViewInject(R.id.btn_add_qrcode)
//	private Button addcodeButton;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		ViewUtils.inject(this);
//
//	}
//
//	@OnClick(R.id.btn_scan_barcode)
//	public void scOnClick(View view) {
//		startActivityForResult(new Intent(this, CaptureActivity.class), 0);
//	}
//
//	@OnClick(R.id.btn_add_qrcode)
//	public void codeOnClick(View view) {
//		String str =qrStrEditText.getText().toString().trim();
//		try {
//			if (str.length()>0){
//				//根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
//				Bitmap  codeBitmap=EncodingHandler.createQRCode(str, 500);
//				qrImgImageView.setImageBitmap(codeBitmap);
//			}else{
//				MyToast.showShort(this, "请输入信息");
//			}
//		} catch (WriterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == 0 && resultCode == RESULT_OK) {
//			Bundle bundle = data.getExtras();
//			String codeinformation = bundle.getString("result");
//			resultTextView.setText(codeinformation);
//		}
//	}
//
//	@Override
//	public int setResourceId() {
//		return R.layout.activity_code_main;
//	}
//
//	@Override
//	public void initData() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public View hideView() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
