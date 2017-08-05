// Upgrade NOTE: replaced '_Object2World' with 'unity_ObjectToWorld'
// Upgrade NOTE: replaced '_World2Object' with 'unity_WorldToObject'

Shader "Custom/GeometryPhong" {
	SubShader{

		//Tags { "RenderType"="Opaque" }

		Pass{

		LOD 200

		CGPROGRAM

#pragma vertex vert
#pragma geometry geo
#pragma fragment frag

#include "UnityCG.cginc"
#include "Lighting.cginc"


	struct vs_in {
		float4 pos: POSITION;
		float4 colour: COLOR;
		float3 normal: NORMAL;
	};

	struct vs_out {
		float4 pos: POSITION;
		float4 colour: COLOR;
		//float3 normal: NORMAL;
	};

	struct fs_in {
		float4 pos: SV_POSITION;		// has to be called this way because of Unity Macro for light
		float4 colour: COLOR;
		//float3 normal: NORMAL;
		//LIGHTING_COORDS(1,2)
		//UNITY_FOG_COORDS(3)
	};

	//uniform float4 _LightColor0;

	//VERTEX-------------------------------------------------------------------------------------------

	vs_out vert(vs_in v) {

		vs_out vout;

		float4 ambientLight = UNITY_LIGHTMODEL_AMBIENT;

		float4 lightDirection = normalize(_WorldSpaceLightPos0);
		float3 normalDirection = normalize(mul(unity_WorldToObject, v.normal));
		float4 diffuseTerm = saturate(dot(lightDirection, normalDirection));
		float4 diffuseLight = diffuseTerm * _LightColor0;
		float4 cameraPosition = normalize(float4(_WorldSpaceCameraPos, 1) - v.pos);
		//Phong
		float4 reflectionVector = reflect(-lightDirection, float4(normalDirection,1));
		float4 specularTerm = pow(saturate(dot(reflectionVector, cameraPosition)),100);
		vout.colour = v.colour * (diffuseLight + ambientLight + specularTerm);
		vout.pos = mul(unity_ObjectToWorld, v.pos);

		//vout.colour = v.colour;

		//vout.colour = float4(1, 1, 1, 1);

		//float3 normalDirection = normalize(mul(_World2Object, v.normal));

		//float3 lightDirection = normalize(_WorldSpaceLightPos0);

		//float3 diffuseReflection = _LightColor0* max(0.0, dot(normalDirection, lightDirection));

		//vout.normal = normalDirection;
		//vout.colour = vout.colour * (float4(diffuseReflection, 1.0) + UNITY_LIGHTMODEL_AMBIENT);

		return vout;
	}

	//GEOMETRY-----------------------------------------------------------------------------------------

	[maxvertexcount(24)]
	void geo(point vs_out voxelPoint[1], inout TriangleStream<fs_in> stream) {
		float size = 0.008;
		fs_in v1, v2, v3, v4, v5, v6, v7, v8;
		float4 corner = voxelPoint[0].pos;
		float4 colour = voxelPoint[0].colour;
		//float3 normal = voxelPoint[0].normal;

		v1.pos = float4(corner.x, corner.y + size, corner.z, corner.w);
		v1.pos = mul(UNITY_MATRIX_VP, v1.pos);
		v1.colour = colour;
		//v1.normal = normal;

		v2.pos = float4(corner.x + size, corner.y + size, corner.z, corner.w);
		v2.pos = mul(UNITY_MATRIX_VP, v2.pos);
		v2.colour = colour;
		//v2.normal = normal;

		v3.pos = float4(corner.x, corner.y, corner.z, corner.w);
		v3.pos = mul(UNITY_MATRIX_VP, v3.pos);
		v3.colour = colour;
		//v3.normal = normal;

		v4.pos = float4(corner.x + size, corner.y, corner.z, corner.w);
		v4.pos = mul(UNITY_MATRIX_VP, v4.pos);
		v4.colour = colour;
		//v4.normal = normal;

		v5.pos = float4(corner.x, corner.y + size, corner.z + size, corner.w);
		v5.pos = mul(UNITY_MATRIX_VP, v5.pos);
		v5.colour = colour;
		//v5.normal = normal;

		v6.pos = float4(corner.x + size, corner.y + size, corner.z + size, corner.w);
		v6.pos = mul(UNITY_MATRIX_VP, v6.pos);
		v6.colour = colour;
		//v6.normal = normal;

		v7.pos = float4(corner.x, corner.y, corner.z + size, corner.w);
		v7.pos = mul(UNITY_MATRIX_VP, v7.pos);
		v7.colour = colour;
		//v7.normal = normal;

		v8.pos = float4(corner.x + size, corner.y, corner.z + size, corner.w);
		v8.pos = mul(UNITY_MATRIX_VP, v8.pos);
		v8.colour = colour;
		//v8.normal = normal;

		stream.Append(v1);
		stream.Append(v2);
		stream.Append(v3);
		stream.Append(v4);
		stream.RestartStrip();

		stream.Append(v2);
		stream.Append(v6);
		stream.Append(v4);
		stream.Append(v8);
		stream.RestartStrip();

		stream.Append(v6);
		stream.Append(v5);
		stream.Append(v8);
		stream.Append(v7);
		stream.RestartStrip();

		stream.Append(v5);
		stream.Append(v1);
		stream.Append(v7);
		stream.Append(v3);
		stream.RestartStrip();

		stream.Append(v5);
		stream.Append(v6);
		stream.Append(v1);
		stream.Append(v2);
		stream.RestartStrip();

		stream.Append(v3);
		stream.Append(v4);
		stream.Append(v7);
		stream.Append(v8);
		stream.RestartStrip();
	}

	//FRAGMENT-----------------------------------------------------------------------------------------

	float4 frag(fs_in v) : COLOR{
		// TODO: Shade based on lighting, probably using 
		// from http://docs.unity3d.com/Manual/SL-BuiltinIncludes.html
		//v.colour = float4(1,1,1,1);
		//v.colour = v.colour * float4(ShadeVertexLights(v.pos, v.normal), 1.0);
		return v.colour;
	//return float4(v.normal, 1.0);

	}

		ENDCG
	}
	}
}
