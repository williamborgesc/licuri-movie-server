package com.dlnapps.dms.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.profile.HeaderDeviceDetailsProvider;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;

import com.dlnapps.dms.service.MediaReceiverRegistrar;
import com.dlnapps.dms.service.MovieContentDirectoryService;

public class LicuriMovieServer implements Runnable {

    private UpnpServiceImpl upnpService;

    public void run() {
	try {

	    upnpService = new UpnpServiceImpl();

	    Runtime.getRuntime().addShutdownHook(new Thread() {
		@Override
		public void run() {
		    upnpService.shutdown();
		}
	    });

	    // Add the bound local device to the registry
	    upnpService.getRegistry().addDevice(createDevice());

	} catch (Exception ex) {
	    System.err.println("Exception occured: " + ex);
	    ex.printStackTrace(System.err);
	    System.exit(1);
	}
    }

    @SuppressWarnings("unchecked")
    private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {

	String deviceName = "Movie Server";

	DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier(deviceName));

	DeviceType type = new UDADeviceType("MediaServer", 1);

	String friendlyName = String.format("Licuri Server: %s", Inet4Address.getLocalHost().getHostName());

	DeviceDetails wmpDetails = new DeviceDetails(friendlyName, new ManufacturerDetails("Dlnapps", "http://www.dlnapps.org/"), new ModelDetails(
		"Windows Media Player Sharing", "Windows Media Player Sharing", "12.0"), "000da201238c", "100000000001",
		"http://www.chii2.org/some_user_interface/", new DLNADoc[] { new DLNADoc("DMS", DLNADoc.Version.V1_5), }, new DLNACaps(new String[] {
			"av-upload", "image-upload", "audio-upload" }));

	// Common Details

	DeviceDetails chii2Details = new DeviceDetails(friendlyName, new ManufacturerDetails("Dlnapps", "http://www.dlnapps.org/"), new ModelDetails(
		"Licuri Server", "Licuri Video Server", "1"), "000da201238c", "100000000001", "http://www.dlnapps.org/some_user_interface/",
		new DLNADoc[] { new DLNADoc("DMS", DLNADoc.Version.V1_5), }, new DLNACaps(new String[] { "av-upload", "image-upload", "audio-upload" }));

	Map<HeaderDeviceDetailsProvider.Key, DeviceDetails> headerDetails = new HashMap<HeaderDeviceDetailsProvider.Key, DeviceDetails>();
	headerDetails.put(new HeaderDeviceDetailsProvider.Key("User-Agent", "FDSSDP"), wmpDetails);
	headerDetails.put(new HeaderDeviceDetailsProvider.Key("User-Agent", "Xbox.*"), wmpDetails);
	headerDetails.put(new HeaderDeviceDetailsProvider.Key("X-AV-Client-Info", ".*PLAYSTATION 3.*"), chii2Details);
	HeaderDeviceDetailsProvider provider = new HeaderDeviceDetailsProvider(chii2Details, headerDetails);

	LocalService<ConnectionManagerService> service = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
	service.setManager(new DefaultServiceManager<ConnectionManagerService>(service, ConnectionManagerService.class));

	LocalService<MovieContentDirectoryService> movieContentDirectory = new AnnotationLocalServiceBinder().read(MovieContentDirectoryService.class);
	movieContentDirectory.setManager(new DefaultServiceManager<MovieContentDirectoryService>(movieContentDirectory, MovieContentDirectoryService.class));

	LocalService<MediaReceiverRegistrar> mediaReceiverRegistrar = new AnnotationLocalServiceBinder().read(MediaReceiverRegistrar.class);
	mediaReceiverRegistrar.setManager(new DefaultServiceManager<MediaReceiverRegistrar>(mediaReceiverRegistrar, MediaReceiverRegistrar.class));

	LocalService<?>[] services = { service, movieContentDirectory, mediaReceiverRegistrar };

	return new LocalDevice(identity, type, provider, createDefaultDeviceIcon(), services);

    }

    @SuppressWarnings("unused")
    private Icon createDefaultDeviceIcon2() {
	return new Icon("image/png", 48, 48, 8, "icon.png",
		"89504E470D0A1A0A0000000D49484452000000300000003008060000005702F9870000034B494441546843EDDA5BE8656318C"
			+ "7F1CF384721651A71251732144ACD8DA6D47021862617CE694C4C0E99901B85422EA4A6910C921C1A29C75C6890C31D65E474"
			+ "314D94682449314E3318FD58ABB6FDDF7BAF77ADFFFCDB6BEBFFD4DBAEBDD7F33ECF773DEFFBAEF7FDADBDC4BFB6046BB11EC"
			+ "B7170F57DDF3EF6E0333C84C7B03789A73D854BFB966D433E5B927392BF1A8FCE58F275BAEB02F0014E9F51806D01F8ADC763"
			+ "BEE9BEEE09C0DEA6ABFAFCFB22C0B4ABB3588159A8C0CF78051FE15BFC801FB10BBF54AB5856B2DDC893329F7FE2AFA105627"
			+ "FA41D58B5837048D50EC561381C47E2689C88D5583AE926350DA137AB2774129F8605EC415C352EF824804F71467587A7917C"
			+ "1D3339BE84F347253109600D5E9866E603B1B3C1CC0D9D63E30032B68FC2EF3D01481A9FE0E4E17CC601BC85B37A947C52C95"
			+ "CB8AE146033AEED19C08DD8580A7017EEEC00702BDEC73B1D7C9B5C2EC273A500A1DDD4D4E388DF9FC095C861E3667CD3A18F"
			+ "712E19D259D6FF63E3E6C06578A643F01A20AE3F21954CD9FFE8D0D7B0CB69D8560A70015EEE107410A076CF19F67ABCDDA1B"
			+ "F419713B0A314E01C6CED10701440DD4D86D52DD8D9A1DFB81C87AF4A0156E18D0E812601CC77582D1B35A7C6CD81850298CF"
			+ "B0EA154076AC0FE0F66AE75A52ECDE00BC8E1BB0BD24EB816B5A019C8D046A6B93E64026E0063CDFB6D3EAFA63F175E9243E0"
			+ "FAF7608340A20C3E57EDC531D803A74FB8FCBF1F8BC14E0623CDB21D230C06BC8537DCEFADDA1EF53F071294044DE873B04A9"
			+ "01BEC44DD541A44337235DCEC4BBA500591D52F2B616E8EF702F7E6DEBDC707D76072F9602642397D2B7B5C8F20B7508BA66D"
			+ "4A818F7208B0A1145A04F761F6E2BAD405488637AA69B6633B8B21420D7ADC07B3D294174A26C020F68031029E3C29E008C1C"
			+ "3EC9AD49D8BA1B77542ADBB458AEC0E395AA372787268038448F79B23A0D7D81EF2B5931D2E1BEB4E41279F1884A5A3C0997E"
			+ "0DC49414A00C6F9679D4F8B2E9AA5B3D646A38FE60859B7BC40D9AF1ABF19C3D147A38B0E6BA39111934F2B9B0F40AB400B75"
			+ "F122C042DDD9D27EFF1715C8E4CB4B8759B4DDA9C087387516B3CF4BFA00ACC323330AB0B6FEB34764C49CC266C99EC6E5F58"
			+ "363F0EF3639BAF5754E64BEE64547FE6E93ED85BF01AB62C65EC4BF94130000000049454E44AE426082");
    }

    private Icon createDefaultDeviceIcon() {
	return new Icon("image/png", 48, 48, 1, "icon.png", "89504e470d0a1a0a0000000d49484452000000300000003008060000005702f98700000009704859730"
		+ "0000ec300000ec301c76fa864000000206348524d00007a25000080830000f9ff000080e90000753000"
		+ "00ea6000003a980000176f925fc54600000b404944415478dac49a7b7054f515c73fbffbdcbb9bdd104"
		+ "2c23b86f09637088aad25688b4a9de9f8a8fc61b59d5adb69abceb4339d3a9d4e5ba79871a6b54e45b4"
		+ "1dc7da875614479d56a0229568081b505ea205846805244280645fd9d7bdf7d73ff6eeba9b171b12ed9"
		+ "9b933c9eebdbf7bbee7777ee77ccf392b1839b90cf822d005dc06ac005245df1bc02ee049c0048e002d"
		+ "fc1f45019603dff3943903c80b5c6ed1dfa73d4077034b01eb62941017f9cc439ec52ff72c5b2215210"
		+ "5cbaf202568bac0ce4a84804c461239eff4b7a60dbce6addbda6be74614c00ae067c0aa3e0b09b8fe96"
		+ "2a744350d76032a1cec0752587df493275960f2ba0d01377d9df9620167568dd16239d727b2fe302ff0"
		+ "21e075e2947217508caaf02fe09ccead712021a66f8a819a7d330cb879d914801fe0a15cbaf72eaa30c"
		+ "efeeed01a0ed8d183d717720834e07567b603e00e2c3dd8110f00c7035e007983d6934e3aa029c381be"
		+ "3584777c9cd9a26a81aa322651e98002149265ce25177e008306d2c52c29ef6d3786744003b8035c0a9"
		+ "8b055009bc04ac04b00c8d6533c7f3c87757525be9e744679417c3c778baf910f15496ee7879ae3baac"
		+ "247d03298581de03babe691755c56ce9d8ca9abfce4a937d9b2e74312a96cfef636e056e0c450018480"
		+ "cdc017f22ef2bb3b1bb97de56cbae26912e92c86a662682a0278fbd869f67d708673b1147f7dfd3d345"
		+ "5293da58ecb1d57cfa13ae86351432d974d1b4bd671d1b5dc7d3d691b01042c9d631ddd7c77dd6b7c78"
		+ "3a927f7c0ff018f0a77201048157bd30998b998a206419dcb47c3ab7afbc9429e342a4b30ea94c2eaaf"
		+ "87415cbd048db0e9d9124a2d7ca52424da585a9a9243336a9ac53728fdfd43c75241f75c658ffca7e5e"
		+ "0c1fedadd7573da35e10c037813f03042b555c1712b14fc35fc86f72e3f2697ca37136b3268d2691ca9"
		+ "2b19dc2828ad2ffb2ae2b914506d1550501e89a4af8c829da3b223cf9dabbc49219ce4593fd2db10db8"
		+ "0e700603700bf02ca0993ec11d77d712ac54d9f68f6ede79aba7d4970326d72daee7ae6be731757c15c"
		+ "97416574a5c290b87b8384a2942208420e43738174bf1df4f222433368f6f3e40dbfb1da4b3a5394251"
		+ "a1ba46239594c4220e400ff063e08f0301a8048e023500d7dc308ad5b78e229d926433923d3be3b46c8"
		+ "dd275d62e79c83234be76c554eefcca3c829641c832a8f0e9b81e0a4508e2a92cd16486684f9aa79b0f"
		+ "71f45417e1c31d7daca7e9399097afa860dc449d652b82ecdd1967c31367f3b7c48185407b7f007e09f"
		+ "c0a60f6028baf7fbb1a55154899b3a0e95348255df6b4c6696b8ed3f949b6e461bfa991b65d6eb8ac81"
		+ "c5536b0b16357595bded6778e5ed0f3035859eb4dd37328dd6987ea98f59f32dea6798589682a20a524"
		+ "91755156cded845787b2c7ffb7a8f829400180f1c04aa8580bb7e329629d34dd229d9c7157c7e85c879"
		+ "8758c421fc7a8cf7f6f590ec7187cc49264c36a81aa371d5aa10c1512ab51374322917272b718b5eabe"
		+ "b82ee2e9b477ffd49fe3d673c0ef672318007819f02cc5e68f1ad7b6a4926dc018fb9a20a14050c5370"
		+ "bc3dc3f9ce2c2daf45899c778876f7e53b3e4b21384a2193925cbad0cfcc791675534d2a820a994ceec"
		+ "c38b61c10acbf42e19fcf76d1b2359affe82d60995674cffcfc1f8dd75562db72d02ce13a12d7013b23"
		+ "193f4967e2250673170788461cf6ef4aa014a501db964c986c30739e859d9528aa4055219376e94994b"
		+ "773a9a4e4ca6b82bcd512279574f31eb338afe217f3dc7cee623fb77d7f4c1fd7299b1d2a609a4a9f6c"
		+ "e3d8924c5af6c90f43119fa5f0ea4bdd6cdf544870776a1eaf9f5760779a4055854747ca13551508256"
		+ "769e992b750bf846f3822a5a4666cb1d3305ef178fd0ff32f587c65804ca64ce53dbe1f8b3a741ccf62"
		+ "98025d1fa69683483a2599bf2cc0e89a02881f695e7927201726c74dccf1f8b22caf0bbaceda3cf1d06"
		+ "9a25d0e7397f8b96a559049f5269a960b81528e2002cf15731e924b1baa471996015c756d88398bfc64"
		+ "cbdc015d17749cc8d0b63d47d9cf7464d9d39ae0dc199b48974375ad4e20a8e66a48e722ebbf5e62fa1"
		+ "44c4bf09f7d4980b496e7f8798586efa7b07f5782fdbb126cdfd4cd95d7845872650555d52ae9b41c34"
		+ "54962bc56eaa149fd6e16e772814a2a1a1a1f07f3ceab2f5a56e1e6bea60f3c66e1231177f855212622"
		+ "fe69017eba98ce4215bbd7a35ededed34353551535383cfe70320d2e5d0bc25c2ba5f9f62f3c62e923d"
		+ "6ece157ca5af97434fe6230b209bcd71a3fbeebb8f93274f120e87b9e9a69b0adf27622ecd9ba33cfc8"
		+ "b0eb6bed4cdfe5d090c2307443704fb762538742089618a2101503e2d5a46e6a00921300c83850b17f2"
		+ "dc73cf71e0c0016ebef966fc7ebf97555db66f8eb0f1a9b3ac6f3ac5c13d09a484398b2c264f31b0b37"
		+ "2d048a4a8a567a05030c7a23922c50886724dd3983f7f3e2fbcf0023b77ee64cd9a350402018f8ec0c7"
		+ "1f65f9fb1fceb27eed277cfc5196d1351aee20ae245d49ac886b294093c74279ebcd18b1a8336045355"
		+ "c59b060011b366c60cb962dac5dbb96bababac2771d27336c7abe0bc71e6c67219b91c5848e7c212a72"
		+ "3e2cf9e0489af94bfdb8ce67934d9b9b9b59b76e1dadadad747797b6642e99662094e282b16ff451145"
		+ "1ec21520376036b810d00470e2659f28540d9c9ac1c711c877038ccfdf7df4f4b4b0be974ba8447d54d"
		+ "3569bc3ec494993e1c470e4ae6da9a63c515e1437952d199ffa427e192f558e370f34226932928be63c"
		+ "78e4294ca37c02e9966d0b8ba9269b32c5c29b1b372d0774a57d2d951e263e7f300f601878159efbf9b"
		+ "e4d8e114d367fbca277545d127af784b4b0b0f3ef820dbb66d2b2dbaabb442bd7be9223f52d25f8fb42"
		+ "fefd204673ab2b4bd51282bcf02afe70174797dfbdf006cdf1465c61c6b48941ae0f8f1e3343535f1c0"
		+ "030fe0ba2ea9d4a79dbad163342e6fac60e95515f8032a8e2349f6b865675fd314bcb935564c454e038"
		+ "78bc9f553c00f8029ff3d9ae2c8bb49662fb072656599b27bf76e76efde5df2d998b13a573406597845"
		+ "8060a542aac72dd40be52aafeb8253c733ec6f2bf47963c03df9289497735eb5ff5b807fff23c2d8093"
		+ "a81a032e4882414a8aad6f8d2b521162c0d60552864d27248c628ae390c53f0e6d628cea77a3c0b6cef"
		+ "0d00609d57dc4c39f1619a1d5ba3dc784735f1e8c0088a7386aa0a96ada860dc2483054bfdf8ac9ce2a"
		+ "98be858e495f7f9158efe27c57b7b0b4db514f0c040f301c71bfbac01cc131f66088d52a99fe1eb37ac"
		+ "3a0e548dd1085428042b556ebdb39a45cb034cae37b16d992b318711c9ac8042fba1147f7db4b3b8467"
		+ "f14787eb001c74960a6d7fde2d0812495552af5d3cd7e414817a6cc30997b5900c314d8d95c6d3c5cb1"
		+ "020aefbf97e46f8f95281ff63ca4e742139a5780d9c09c3c886048a37e86d96f41e2d85c3086971f8a7"
		+ "3ca1f3998e2e9c73ac9a40b8bb602377867f58223261778019891ef581c3a902450a132b1de4051c488"
		+ "9588bd9b65aa22387230c9338f7716ef78d8eb4a47863aa111c0dfc8cd7d01a81daff3ad7b6ba9aed5b"
		+ "0b3b9a6efb080c85c7340d705c984cbd38f77d27eb864d2d3eacdcca2173b2313c05f80db0bfee95758"
		+ "b43cc0158d41c64dd20bdb6cdbb2ecdc27945cd4d20d41f7399bb75be3845f8ff59ea36d076eeccff24"
		+ "3018057f87f19f83d505ff830a03077899fe55787706cc9843a1d45156406a206127453a0198278c4a5"
		+ "fbbcc33bbb13ec0dc789749584ea18702ff032d03ddc2965b1ccf016be8bde036e01f396f8193b41e7f"
		+ "2c6206e3fac52d7057bc3094e9fcad275d6e6d8a1547f67ef79e0618f257f667209b96174925e3f2710"
		+ "0269fa94fe2f4b918a2a06fa19c273c50de6cf430450073c416eba7e940bff56a2f8ea0036011b81b9f"
		+ "db082b295182969f0ae89c0cf07a8ab2ce01160af17cfdf19b615e588362f3f7ff9df00f919a9e1a8ca" + "5b960000000049454e44ae426082");
    }

    public UpnpServiceImpl getUpnpService() {
	return upnpService;
    }
}